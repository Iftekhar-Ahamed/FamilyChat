using FamilyChatAPI.CacheData;
using FamilyChatAPI.DbContexts.Read;
using FamilyChatAPI.DbContexts.Write;
using FamilyChatAPI.Dtos;
using FamilyChatAPI.IRepository;
using FamilyChatAPI.Models.Write;

namespace FamilyChatAPI.Repository
{
    public class LastMessageList : ILastMessages
    {

        private readonly ReadDbContext _contextR;
        private readonly WriteDbContext _contextW;
        private readonly ILastMessageCache _lastMessages;
        public LastMessageList(ReadDbContext readDbContext, WriteDbContext writeDbContext, ILastMessageCache lastMessages)
        {
            _contextR = readDbContext;
            _contextW = writeDbContext;
            _lastMessages = lastMessages;
        }
        public List<ChatMessageDto> GetMessageByChatId(long chatId)
        {
            return _lastMessages.GetMessageByChatId(chatId);
        }
        public bool AddMessageByChatId(byte ChatId, byte IntUserId, string StrMessage, DateTime dateTimeMsg)
        {
            long count = _lastMessages.AddMessageByChatId(ChatId, IntUserId, StrMessage,dateTimeMsg);
            if (count >= 20)
            {
                List<TblMessage> messages = new List<TblMessage>();
                var lastmessages = _lastMessages.GetMessageByChatId(ChatId);
                foreach (var item in lastmessages)
                {
                    var msg = new TblMessage
                    {
                        IntChatId = (byte)item.chatId,
                        IntUserId = (byte)item.chatId,
                        StrMessage = item.messageText,
                        DteMessageDateTime = item.messageDateTime,
                        IsActive = true
                    }; messages.Add(msg);
                }
                _contextW.AddRange(messages);
                _contextW.SaveChanges();

            }
            return true;
        }
    }
}
