using FamilyChatAPI.Dtos;
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
    }
}
