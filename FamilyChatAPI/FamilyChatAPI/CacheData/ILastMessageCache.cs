using FamilyChatAPI.Dtos;

namespace FamilyChatAPI.CacheData
{
    public interface ILastMessageCache
    {
        List<ChatMessageDto> GetMessageByChatId(long chatId);
        long AddMessageByChatId(byte ChatId, byte IntUserId, string StrMessage, DateTime dateTimeMsg);
        bool DeleteMessageByChatId(long ChatId);
        Task<bool> BackupCacheToDatabase();
        Task<bool> UpdateLastMessagesCacheByChatID(long chatId);
    }
}
