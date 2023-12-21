using System;

namespace FamilyChatAPI.DTO
{
    public class UserDto
    {
        public string UserName { get; set; }
        public string PassWord { get; set; }
        public string ConnectionId { get; set; }
        public bool IsUser {  get; set; }
        public long UserId { get; set; }
    }
}
