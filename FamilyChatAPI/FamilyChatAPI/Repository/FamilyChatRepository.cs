using FamilyChatAPI.DbContexts.Read;
using FamilyChatAPI.DbContexts.Write;
using FamilyChatAPI.DTO;
using FamilyChatAPI.Dtos;
using FamilyChatAPI.Helper;
using FamilyChatAPI.IRepository;
using FamilyChatAPI.Models.Write;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Data.SqlClient;
using Microsoft.EntityFrameworkCore;
using Swashbuckle.AspNetCore.SwaggerGen;
using static Microsoft.EntityFrameworkCore.DbLoggerCategory;
using static Microsoft.EntityFrameworkCore.DbLoggerCategory.Database;

namespace FamilyChatAPI.Repository
{
    public class FamilyChatRepository : IFamilyChat
    {
        private readonly ReadDbContext _contextR;
        private readonly WriteDbContext _contextW;
        private readonly IJwtToken _jwtToken;
        private readonly ILastMessages _lastMessageList;
        public FamilyChatRepository(ReadDbContext readDbContext, WriteDbContext writeDbContext, IJwtToken jwtToken,ILastMessages lastMessageList)
        {
            _contextR = readDbContext;
            _contextW = writeDbContext;
            _jwtToken = jwtToken;
            _lastMessageList = lastMessageList;
        }
        public async Task<UserDto> GetUserById(long UserId)
        {
            try
            {
                var data = await _contextR.TblUsers.Where(x => x.IntUserId == UserId && x.IsActive == true).Select( u =>
                    new UserDto
                    {
                        UserId = u.IntUserId,
                        UserName = u.StrUserName,
                        IsUser = true,
                        PassWord = string.Empty,
                        ConnectionId = u.StrConnectionId?? String.Empty
                    }).FirstOrDefaultAsync();
                return data;
            }catch (Exception ex)
            {
                throw ex;
            }
        }
        public async Task<MessageHelper> CreateUser(CreateUserDto UserInformation)
        {
            try
            {
                var data = new Models.Write.TblUser
                {
                    StrUserName = UserInformation.UserName,
                    StrPassword = UserInformation.Password,
                    IsActive = true
                };
                await _contextW.AddAsync(data);
                await _contextW.SaveChangesAsync();
                MessageHelper msg = new MessageHelper();
                msg.Message = "Registration Compelete";
                msg.StatusCode = 200;
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
                msg.Message = "Welcome " + UserName;
                UserDto usr = new UserDto();

                if (data != null) {
                    msg.UserId = data.IntUserId;
                    usr.UserName = data.StrUserName;
                    usr.UserId = data.IntUserId;
                    msg.Token = _jwtToken.GenerateToken(usr);
                }
                else
                {
                    throw new Exception("Wrong Username and Password");
                }
                
                msg.StatusCode = 200;
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

                var res = await _contextR.TblChats.Where(x => (x.IntFromUserId == from && x.IntToUserId == to) || (x.IntToUserId == from && x.IntFromUserId == to) && x.IsAcitve == true).FirstOrDefaultAsync();

                if (res == null)
                {
                    var data = new TblChat
                    {
                        IntFromUserId = ((byte)from),
                        IntToUserId = ((byte)to),
                        IsAcitve = true
                    };
                    await _contextW.AddAsync(data);
                    if (await _contextW.SaveChangesAsync() > 0)
                    {
                        messageHelper.Message = "Successfully Added";
                        messageHelper.StatusCode = 200;
                    }
                    else
                    {
                        throw new Exception("Database Connection Problem");
                    }

                }
                else
                {
                    throw new Exception("Already have Connection");
                }

                return messageHelper;

            } catch (Exception ex)
            {
                throw ex;
            }
        }
        public async Task<List<ConnectionListDto>> GetAllConnectionByUserId(long id)
        {

            try
            {


                var listOfConnectionList = await (from c in _contextR.TblChats
                                                  where (c.IntFromUserId == id || c.IntToUserId == id)
                                                  && c.IsAcitve == true
                                                  select new ConnectionListDto()
                                                  {
                                                      ChatId = c.IntChatId,
                                                      ChatFriendId = c.IntToUserId == id ? c.IntFromUserId : c.IntToUserId,
                                                  }).ToListAsync();
                return listOfConnectionList;
            } catch (Exception ex)
            {
                throw ex;
            }
        }
        public async Task<MessageHelper> SaveMessageByChatId(List<SaveMessageDto> msgList)
        {
            try
            {
                MessageHelper msg = new MessageHelper();
                List<TblMessage> tblMessages = new List<TblMessage>();
                foreach(var m in msgList)
                {
                    var data = new TblMessage
                    {
                        StrMessage = m.Message,
                        IntChatId = m.ChatId,
                        IntUserId = m.SenderId,
                        IsActive = true
                    };
                    tblMessages.Add(data);
                }
                await _contextW.AddRangeAsync(tblMessages);
                if(await _contextW.SaveChangesAsync() <= 0)
                {
                    msg.Message = "Faild to Save";
                }
                msg.StatusCode = 200;
                return msg;

            }catch (Exception ex)
            {
                throw ex;
            }
        }
        public async Task<AllMessageByChatIdDto> GetAllMessageByChatId(long ChatId, long ChatFriendId)
        {
            var result = new AllMessageByChatIdDto();
            
            result.UserFriend = await _contextR.TblUsers.Where(u => u.IntUserId == ChatFriendId).Select(u => new UserDto
            {
                UserId = u.IntUserId,
                UserName = u.StrUserName,
                IsUser = false,
                PassWord = string.Empty,
                ConnectionId = u.StrConnectionId ?? String.Empty
            }).FirstOrDefaultAsync();
            result.ChatMessages = _lastMessageList.GetMessageByChatId(ChatId);
            if (result.ChatMessages.Count < 15)
            {
                int take = 15 - result.ChatMessages.Count;
                result.ChatMessages.AddRange(await _contextR.TblMessages.Where(m => m.IntChatId == ChatId && m.IsActive == true).Select(m => new ChatMessageDto
                {
                    messageId = m.IntMessageId,
                    messageText = m.StrMessage,
                    chatId = m.IntChatId,
                    UserId = m.IntUserId,
                    messageDateTime = m.DteMessageDateTime,
                }).OrderBy(m => m.messageId).Take(take).ToListAsync());
            }
            if (result.ChatMessages.Count == 0)
            {
                result.ChatMessages.Add(new ChatMessageDto { messageText = "" });
            }
            return result;
        }
    }
}
