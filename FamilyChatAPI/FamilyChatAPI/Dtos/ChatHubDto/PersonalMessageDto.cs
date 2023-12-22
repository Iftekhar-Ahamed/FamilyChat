namespace FamilyChatAPI.Dtos.ChatHubDto
{
    public class PersonalMessageDto
    {
        public string connectionId { get; set; }
        public bool isUser { get; set; }
        public string passWord { get; set; }
        public int userId { get; set; }
        public string userName { get; set; }
        public int chatId { get; set; }
        public string messageText { get; set; }
    }
}
