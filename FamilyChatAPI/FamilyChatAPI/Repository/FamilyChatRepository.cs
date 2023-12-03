using FamilyChatAPI.Dtos;
using FamilyChatAPI.Helper;
using FamilyChatAPI.IRepository;

namespace FamilyChatAPI.Repository
{
    public class FamilyChatRepository : IFamilyChat
    {
        public FamilyChatRepository() { 
        }
        public MessageHelper CreateUser(CreateUserDto UserInfotmation)
        {
            try
            {
                MessageHelper msg = new MessageHelper();
                msg.message = "OK";
                msg.statusCode = 200;
                return msg;
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
    }
}
