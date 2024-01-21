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
       
        
        public async Task<bool> AddMessageByChatId(byte ChatId, byte IntUserId, string StrMessage, DateTime dateTimeMsg)
        {
            long count = _lastMessages.AddMessageByChatId(ChatId, IntUserId, StrMessage,dateTimeMsg);
            if (count >= 20)
            {
                await _lastMessages.UpdateLastMessagesCacheByChatID(ChatId);
                _lastMessages.DeleteMessageByChatId(ChatId);
            }
            return true;
        }
    }
}
