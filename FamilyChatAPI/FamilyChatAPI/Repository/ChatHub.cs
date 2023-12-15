using FamilyChatAPI.DbContexts.Read;
using FamilyChatAPI.DbContexts.Write;
using FamilyChatAPI.Dtos;
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
        public ChatHub(ReadDbContext contextR,WriteDbContext contextW)
        {
            _contextR = contextR;
            _contextW = contextW;
        }
        public async Task SendNotificationToAll(string message)
        {
            await Clients.All.SendAsync("broadcastMessage", message);
        }
        public async Task SendNotificationToClient(string message, byte id)
        {
            var hubConnections = await _contextR.TblUsers.Where(con => con.IntUserId == id && con.IsActive == true && con.StrConnectionId!=null).ToListAsync();
            foreach (var hubConnection in hubConnections)
            {
                await Clients.Client(hubConnection.StrConnectionId??"").SendAsync("ReceivedPersonalNotification", message, hubConnection.StrUserName);
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
                                        select user.StrConnectionId).ToListAsync();

            var obj = await _contextR.TblUsers.Where(u => u.IntUserId == UserId).Select(u => new ConnectionUpdateDto
            {
                IsUser = false,
                ConnectionId = u.StrConnectionId ?? "",
                UserId = u.IntUserId,
                Name = u.StrUserName
            }).FirstOrDefaultAsync();

            string data = string.Empty;
            if (obj != null)
            {
                data = JsonConvert.SerializeObject(obj, Formatting.Indented);
            }

            foreach (var hubConnection in hubConnections)
            {
                await Clients.Client(hubConnection).SendAsync("ActiveUser", data);
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
