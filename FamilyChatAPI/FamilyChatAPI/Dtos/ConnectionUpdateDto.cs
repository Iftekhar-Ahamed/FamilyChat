namespace FamilyChatAPI.Dtos
{
    public class ConnectionUpdateDto
    {
        public string ConnectionId { get; set; }
        public string Name { get; set; }
        public byte UserId { get; set; }
        public byte ChatId { get; set; }
        public bool IsUser { get; set; }
    }
}
