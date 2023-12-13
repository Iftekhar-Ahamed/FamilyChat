using FamilyChatAPI.IRepository;
using FamilyChatAPI.Models.Read;
using TableDependency.SqlClient;
namespace FamilyChatAPI.Repository
{
    public class SubscribeNotificationTableDependencyRepository:ISubscribeNotificationTableDependency
    {
        private SqlTableDependency<TblChat> _dtblChat;
        private ChatHub _chatHub;
        public SubscribeNotificationTableDependencyRepository(ChatHub chatHub)
        {
            _chatHub = chatHub;
        }
        public void SubscribeTableDependency(string connectionString)
        {
            _dtblChat = new SqlTableDependency<TblChat>(connectionString);
            _dtblChat.OnChanged += TableDependency_OnChanged;
            _dtblChat.OnError += TableDependency_OnError;
            _dtblChat.Start();
        }

        private void TableDependency_OnError(object sender, TableDependency.SqlClient.Base.EventArgs.ErrorEventArgs e)
        {
            Console.WriteLine($"{nameof(TblChat)} SqlTableDependency error: {e.Error.Message}");
        }

        private async void TableDependency_OnChanged(object sender, TableDependency.SqlClient.Base.EventArgs.RecordChangedEventArgs<TblChat> e)
        {
            if (e.ChangeType != TableDependency.SqlClient.Base.Enums.ChangeType.None)
            {
                var notification = e.Entity;
                await _chatHub.SendNotificationToClient("New Connection", notification.IntToUserId);
                
            }
        }
    }
}
