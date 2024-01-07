namespace FamilyChatAPI.Dtos
{
    public class ChatMessageDto
    {
        public long messageId {  get; set; }
        public String messageText {  get; set; }
        public int chatId { get; set; }
        public byte UserId { get; set; }
    }
}
