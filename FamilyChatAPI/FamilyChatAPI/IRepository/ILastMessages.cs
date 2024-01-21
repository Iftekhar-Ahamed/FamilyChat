using FamilyChatAPI.Dtos;
using FamilyChatAPI.Models.Write;

namespace FamilyChatAPI.IRepository
{
    public interface ILastMessages
    {
        List<ChatMessageDto> GetMessageByChatId(long chatId);
        Task<bool> AddMessageByChatId(byte ChatId, byte IntUserId, string StrMessage,DateTime dateTimeMsg);
    }
}
