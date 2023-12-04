using FamilyChatAPI.DbContexts.Read;
using FamilyChatAPI.DbContexts.Write;
using FamilyChatAPI.DTO;
using FamilyChatAPI.Dtos;
using FamilyChatAPI.Helper;
using FamilyChatAPI.IRepository;
using Microsoft.EntityFrameworkCore;

namespace FamilyChatAPI.Repository
{
    public class FamilyChatRepository : IFamilyChat
    {
        private readonly ReadDbContext _contextR;
        private readonly WriteDbContext _contextW;
        private readonly IJwtToken _jwtToken;
        public FamilyChatRepository(ReadDbContext readDbContext, WriteDbContext writeDbContext,IJwtToken jwtToken)
        {
            _contextR = readDbContext;
            _contextW = writeDbContext;
            _jwtToken = jwtToken;
        }
        public async Task<MessageHelper> CreateUser(CreateUserDto UserInfotmation)
        {
            try
            {
                var data = new Models.Write.TblUser
                {
                    StrUserName = UserInfotmation.UserName,
                    StrPassword = UserInfotmation.Password,
                    IsActive = true
                };
                await _contextW.AddAsync(data);
                await _contextW.SaveChangesAsync();
                MessageHelper msg = new MessageHelper();
                msg.message = "Registration Compelete";
                msg.statusCode = 200;
                return msg;
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        public async Task<MessageHelper> UserLogIn(string UserName, string Password)
        {
            try
            {
                var data = await _contextR.TblUsers.Where(x => x.StrUserName == UserName && x.StrPassword == Password).FirstOrDefaultAsync();
                
                
                MessageHelper msg = new MessageHelper();
                msg.message = data!=null ? "Welcome "+UserName: "Wrong Username and Password";
                UserDto usr = new UserDto();

                if (data != null) {
                    usr.UserName = data.StrUserName;
                    usr.UserId = data.IntUserId;
                    msg.Token = _jwtToken.GenerateToken(usr);
                }

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
