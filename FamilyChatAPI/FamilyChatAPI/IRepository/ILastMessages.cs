using FamilyChatAPI.Dtos;

namespace FamilyChatAPI.IRepository
{
    public interface ILastMessages
    {
        List<ChatMessageDto> GetMessageByChatId(long chatId);
        bool AddMessageByChatId(byte ChatId, byte IntUserId, string StrMessage,DateTime dateTimeMsg);
    }
}
