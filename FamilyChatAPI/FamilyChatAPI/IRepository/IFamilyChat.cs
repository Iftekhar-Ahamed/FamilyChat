using FamilyChatAPI.Dtos;
using FamilyChatAPI.Helper;
using System.Threading.Tasks;

namespace FamilyChatAPI.IRepository
{
    public interface IFamilyChat
    {
        MessageHelper CreateUser(CreateUserDto UserInfotmation);
    }
}
