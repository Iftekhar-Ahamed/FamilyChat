using FamilyChatAPI.DbContexts.Write;
using FamilyChatAPI.Dtos;
using FamilyChatAPI.Dtos.ChatHubDto;
using FamilyChatAPI.IRepository;
using FamilyChatAPI.Models.Write;
using FamilyChatAPI.Repository;
using Microsoft.Extensions.Caching.Memory;

namespace FamilyChatAPI.CacheData
{
    public class LastMessagesCache : ILastMessageCache
    {
        private Dictionary<long, List<TblMessage>> _lastTblMessages;
        private readonly WriteDbContext _contextW;

        public LastMessagesCache(IMemoryCache memoryCache,WriteDbContext writeDbContext)
        {
            _contextW = writeDbContext;
            if (memoryCache.TryGetValue("_lastTblMessages", out Dictionary<long, List<TblMessage>> lastTblMessages))
            {
                _lastTblMessages = lastTblMessages;
            }
            else
            {
                _lastTblMessages = new Dictionary<long, List<TblMessage>>();
                var cacheEntryOption = new MemoryCacheEntryOptions().SetPriority(CacheItemPriority.NeverRemove);
                memoryCache.Set("_lastTblMessages", _lastTblMessages, cacheEntryOption);
            }
        }
        public async Task<bool> UpdateLastMessagesCacheByChatID(long chatId)
        {
            List<TblMessage> messages = _lastTblMessages[chatId];
            await _contextW.AddRangeAsync(messages);
            await _contextW.SaveChangesAsync();
            return true;
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
                    UserId = m.IntUserId,
                    messageDateTime = m.DteMessageDateTime
                }).OrderByDescending(m => m.messageDateTime).ToList();
                return result;
            }
            return result;
        }
        public long AddMessageByChatId(byte ChatId, byte IntUserId, string StrMessage, DateTime dateTimeMsg)
        {
            if (_lastTblMessages.ContainsKey(ChatId) == false)
            {
                _lastTblMessages.Add(ChatId, new List<TblMessage>());
            }
            _lastTblMessages[ChatId].Add(new TblMessage
            {
                IntChatId = ChatId,
                IntUserId = IntUserId,
                StrMessage = StrMessage,
                DteMessageDateTime = dateTimeMsg,
                IsActive = true
            });
            return _lastTblMessages[ChatId].Count();
        }
        public bool DeleteMessageByChatId(long ChatId)
        {
            _lastTblMessages[ChatId] = new List<TblMessage>();
            return true;
        }
        public async Task<bool> BackupCacheToDatabase()
        {
            try
            {
                foreach (var key in _lastTblMessages)
                {
                    await _contextW.AddRangeAsync(key.Value);
                }
                _lastTblMessages.Clear();
                await _contextW.SaveChangesAsync();
                
            }catch (Exception ex)
            {
                throw ex;
            }
            return true;
        }
    }

}
