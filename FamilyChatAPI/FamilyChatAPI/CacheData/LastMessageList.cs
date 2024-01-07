using FamilyChatAPI.DbContexts.Read;
using FamilyChatAPI.DbContexts.Write;
using FamilyChatAPI.Dtos;
using FamilyChatAPI.Models.Write;

namespace FamilyChatAPI.CacheData
{
    public class LastMessageList
    {
        private static Dictionary<long, List<TblMessage>> _lastTblMessages = new Dictionary<long, List<TblMessage>>();
        private readonly ReadDbContext _contextR;
        private readonly WriteDbContext _contextW;
        public LastMessageList(ReadDbContext readDbContext, WriteDbContext writeDbContext) {
            _contextR = readDbContext;
            _contextW = writeDbContext;

        }
        public List<ChatMessageDto> GetMessageByChatId(long chatId)
        {
            List<ChatMessageDto> result = new List<ChatMessageDto>();
            if (_lastTblMessages.ContainsKey(chatId))
            {
                result = _lastTblMessages[chatId].Where(m => m.IsActive == true).Select(m => new ChatMessageDto
                {
                    messageId = m.IntMessageId,
                    messageText = m.StrMessage,
                    chatId = m.IntChatId,
                    UserId = m.IntUserId
                }).OrderByDescending(m => m.messageId).Take(15).ToList();
                if (result.Count == 0)
                {
                    result.Add(new ChatMessageDto { messageText = "" });
                }
                return result;
            }
            return result;
        }
        public  bool AddMessageByChatId(byte ChatId, byte IntUserId, string StrMessage)
        {
            if (_lastTblMessages.ContainsKey(ChatId) == false)
            {
                _lastTblMessages.Add(ChatId, new List<TblMessage>());
            }
            var id = _lastTblMessages[ChatId].Count();
            if(id == 0)
            {
                _lastTblMessages[ChatId] = new List<TblMessage>();
            }

            _lastTblMessages[ChatId].Add( new TblMessage
            {
                IntMessageId = id,
                IntChatId = ChatId,
                IntUserId = IntUserId,
                StrMessage = StrMessage,
                IsActive = true
            });

            if(_lastTblMessages[ChatId].Count == 20)
            {
                List<TblMessage> messages = new List<TblMessage>();
                foreach(var item in _lastTblMessages[ChatId]){
                    var msg = new TblMessage
                    {
                        IntChatId = item.IntChatId,
                        IntUserId = item.IntUserId,
                        StrMessage = item.StrMessage,
                        IsActive = item.IsActive
                    }; messages.Add(msg);
                }
                 _contextW.AddRange(messages);
                _contextW.SaveChanges();
                _lastTblMessages[ChatId] = new List<TblMessage>();
            }

            return true;
        }
    }
}
