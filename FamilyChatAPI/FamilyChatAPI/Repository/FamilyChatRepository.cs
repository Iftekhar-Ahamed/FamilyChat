using FamilyChatAPI.DbContexts.Read;
using FamilyChatAPI.DbContexts.Write;
using FamilyChatAPI.DTO;
using FamilyChatAPI.Dtos;
using FamilyChatAPI.Helper;
using FamilyChatAPI.IRepository;
using FamilyChatAPI.Models.Write;
using Microsoft.EntityFrameworkCore;
using Swashbuckle.AspNetCore.SwaggerGen;
using static Microsoft.EntityFrameworkCore.DbLoggerCategory.Database;

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
        public async Task<MessageHelper> CreateNewConnection(long from, long to)
        {
            try
            {
                MessageHelper messageHelper = new MessageHelper();

                var res = await _contextR.TblChats.Where(x => (x.IntFromUserId == from || x.IntToUserId == from) && x.IsAcitve == true).FirstOrDefaultAsync();

                if (res!=null)
                {
                    var data = new TblChat
                    {
                        IntFromUserId = ((byte)from),
                        IntToUserId = ((byte)to),
                        IsAcitve = true
                    };
                    await _contextW.AddAsync(data);
                    if ( await _contextW.SaveChangesAsync()> 0)
                    {
                        messageHelper.message = "Successfully Added";
                        messageHelper.statusCode = 200;
                    }
                    else
                    {
                        messageHelper.message = "Database Connection Problem";
                        messageHelper.statusCode = 400;
                    }
                    
                }
                else
                {
                    throw new Exception("Already have Connection");
                }

                return messageHelper;

            }catch (Exception ex)
            {
                throw ex;
            }
        }
        public async Task<List<ConnectionListDto>> GetAllConnectionByUserId(long id)
        {

            try
            {

                var listOfConnection = await (from c in _contextR.TblChats
                                              join u in _contextR.TblUsers on (c.IntFromUserId==id?c.IntToUserId:c.IntFromUserId) equals u.IntUserId
                                              where (c.IntFromUserId == id || c.IntToUserId == id)
                                              && c.IsAcitve == true
                                              select new ConnectionListDto()
                                              {
                                                  ChatId = c.IntChatId,
                                                  ChatName = u.StrUserName
                                              }).ToListAsync();
                return listOfConnection;
            } catch (Exception ex)
            {
                throw ex;
            }
        }
    }
}
