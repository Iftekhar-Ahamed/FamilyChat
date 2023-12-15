namespace FamilyChatAPI.Dtos
{
    public class SaveMessageDto
    {
        public byte ChatId { get; set; }
        public string Message { get; set; }
        public byte SenderId { get; set; }
    }
}
