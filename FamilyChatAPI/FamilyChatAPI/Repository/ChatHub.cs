using FamilyChatAPI.DbContexts.Read;
using FamilyChatAPI.DbContexts.Write;
using FamilyChatAPI.Dtos;
using FamilyChatAPI.Dtos.ChatHubDto;
using FamilyChatAPI.IRepository;
using FamilyChatAPI.Models.Read;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using Newtonsoft.Json;
using System.Linq;
using TableDependency.SqlClient.Base.Messages;

namespace FamilyChatAPI.Repository
{
    public class ChatHub : Hub
    {
        private readonly ReadDbContext _contextR;
        private readonly WriteDbContext _contextW;
        private readonly ILastMessages _lastMessageList;
        public ChatHub(ReadDbContext contextR,WriteDbContext contextW,ILastMessages lastMessageList)
        {
            _contextR = contextR;
            _contextW = contextW;
            _lastMessageList = lastMessageList;
        }
        public async Task SendNotificationToAll(string message)
        {
            await Clients.All.SendAsync("broadcastMessage", message);
        }
        public async Task SendNotificationToClient(string message)
        {
            try
            {
                PersonalMessageDto personalMessageDto = Newtonsoft.Json.JsonConvert.DeserializeObject<PersonalMessageDto>(message);

                if (await _lastMessageList.AddMessageByChatId(personalMessageDto.chatId, personalMessageDto.userId, personalMessageDto.messageText, personalMessageDto.messageDateTime))
                {
                    await Clients.Client(personalMessageDto.connectionId).SendAsync("ReceivedPersonalNotification", message);
                }

                
            }catch (Exception ex)
            {
                throw ex;
            }
        }
        public async Task NotifyOnConnectionIdUpdate(byte UserId)
        {
            var hubConnections = await (from chat in _contextR.TblChats
                                        join user in _contextR.TblUsers on (byte)(chat.IntFromUserId == UserId ? chat.IntToUserId : chat.IntFromUserId) equals user.IntUserId
                                        where chat.IsAcitve == true
                                        && (chat.IntToUserId == UserId || chat.IntFromUserId == UserId)
                                        && user.IsActive == true
                                        && user.StrConnectionId != null
                                        select new { user.StrConnectionId,chat.IntChatId}).ToListAsync();

            var obj = await _contextR.TblUsers.Where(u => u.IntUserId == UserId).Select(u => new ConnectionUpdateDto
            {
                IsUser = false,
                ConnectionId = u.StrConnectionId ?? "",
                UserId = u.IntUserId,
                Name = u.StrUserName
            }).FirstOrDefaultAsync();

            

            foreach (var hubConnection in hubConnections)
            {
                var t = obj;
               
                string data = string.Empty;
                if (t != null)
                {
                    t.ChatId = hubConnection.IntChatId;
                    data = JsonConvert.SerializeObject(t, Formatting.Indented);
                }
                await Clients.Client(hubConnection.StrConnectionId).SendAsync("ActiveUser", data);
            }
        }
        public override Task OnConnectedAsync()
        {
            Clients.Caller.SendAsync("OnConnected");
            return base.OnConnectedAsync();
        }
        public async Task SaveUserConnection(byte id)
        {
            var connectionId = Context.ConnectionId;
            var data = await _contextW.TblUsers.Where(con => con.IntUserId == id && con.IsActive == true).FirstOrDefaultAsync();
            
            data.StrConnectionId = connectionId;
            _contextW.Update(data);
            await _contextW.SaveChangesAsync();
        }
        public override Task OnDisconnectedAsync(Exception? exception)
        {
            var hubConnection = _contextW.TblUsers.FirstOrDefault(con => con.StrConnectionId == Context.ConnectionId);
            if (hubConnection != null)
            {
                hubConnection.StrConnectionId=null;
                _contextW.TblUsers.Update(hubConnection);
                _contextW.SaveChangesAsync();
            }

            return base.OnDisconnectedAsync(exception);
        }
    }
}
