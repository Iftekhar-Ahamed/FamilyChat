using FamilyChatAPI.Dtos;
using FamilyChatAPI.Helper;
using System.Threading.Tasks;

namespace FamilyChatAPI.IRepository
{
    public interface IFamilyChat
    {
        Task<MessageHelper> CreateUser(CreateUserDto UserInfotmation);
        Task<MessageHelper> UserLogIn(string UserName, string Password);
    }
}
