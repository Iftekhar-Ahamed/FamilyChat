using FamilyChatAPI.Dtos;
using FamilyChatAPI.IRepository;
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
            return Ok(_IFamilyChat.CreateUser(UserInfotmation));
        }
    }
}
