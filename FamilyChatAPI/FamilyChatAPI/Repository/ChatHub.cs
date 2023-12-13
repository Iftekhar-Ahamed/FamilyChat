using FamilyChatAPI.DbContexts.Read;
using FamilyChatAPI.DbContexts.Write;
using FamilyChatAPI.Models.Read;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;

namespace FamilyChatAPI.Repository
{
    public class ChatHub : Hub
    {
        private readonly ReadDbContext _contextR;
        private readonly WriteDbContext _contextW;
        public ChatHub(ReadDbContext contextR,WriteDbContext contextW)
        {
            Console.WriteLine("In Constructor ChatHub");
            _contextR = contextR;
            _contextW = contextW;
        }
        public async Task SendNotificationToAll(string message)
        {
            await Clients.All.SendAsync("broadcastMessage", message);
        }
        public async Task SendNotificationToClient(string message, byte id)
        {
            var hubConnections = _contextR.TblUsers.Where(con => con.IntUserId == id && con.IsActive == true && con.StrConnectionId!=null).ToList();
            foreach (var hubConnection in hubConnections)
            {
                await Clients.Client(hubConnection.StrConnectionId??"").SendAsync("ReceivedPersonalNotification", message, hubConnection.StrUserName);
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
