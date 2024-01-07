using FamilyChatAPI.DTO;

namespace FamilyChatAPI.Dtos
{
    public class AllMessageByChatIdDto
    {
        public UserDto? UserFriend { get; set; }
        public List<ChatMessageDto> ChatMessages { get; set; }

    }
}
