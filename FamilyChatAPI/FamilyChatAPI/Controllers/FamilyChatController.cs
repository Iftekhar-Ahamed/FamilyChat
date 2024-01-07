using FamilyChatAPI.DTO;
using FamilyChatAPI.Dtos;
using FamilyChatAPI.Helper;
using FamilyChatAPI.IRepository;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace FamilyChatAPI.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class FamilyChatController : ControllerBase
    {
        public IFamilyChat _IFamilyChat;
        public FamilyChatController(IFamilyChat IFamilyChat)
        {
            _IFamilyChat = IFamilyChat;
        }
        [HttpPost]
        [Route("CreateUser")]
        public async Task<IActionResult> CreateUser(CreateUserDto UserInfotmation)
        {
           var dt = await _IFamilyChat.CreateUser(UserInfotmation);
            return Ok(dt);
        }
        [HttpGet]
        [Route("UserLogIn")]
        public async Task<IActionResult> UserLogIn(string UserName,string Password)
        {
            var dt = await _IFamilyChat.UserLogIn(UserName,Password);
            return Ok(dt);
        }
        [Authorize]
        [HttpGet]
        [Route("GetUserById")]
        public async Task<IActionResult> GetUserById(long UserId)
        {
            var dt = await _IFamilyChat.GetUserById(UserId);
            return Ok(dt);
        }
        [Authorize]
        [HttpGet]
        [Route("GetAllConnectionByUserId")]
        public async Task<IActionResult> GetAllConnectionByUserId(long id)
        {
            return Ok(await _IFamilyChat.GetAllConnectionByUserId(id));
        }
        [Authorize]
        [HttpGet]
        [Route("CreateNewConnection")]
        public async Task<IActionResult> CreateNewConnection(long from, long to)
        {
            return Ok(await _IFamilyChat.CreateNewConnection(from,to));
        }
        [Authorize]
        [HttpPost]
        [Route("SaveMessageByChatId")]
        public async Task<IActionResult> SaveMessageByChatId(List<SaveMessageDto> msgList)
        {
            return Ok(await _IFamilyChat.SaveMessageByChatId(msgList));
        }
        //[Authorize]
        [HttpGet]
        [Route("GetAllMessageByChatId")]
        public async Task<IActionResult> GetAllMessageByChatId(long ChatId,long ChatFriendId)
        {
            return Ok(await _IFamilyChat.GetAllMessageByChatId(ChatId,ChatFriendId));
        }
    }
}
