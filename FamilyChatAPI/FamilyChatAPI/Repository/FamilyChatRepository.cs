﻿using FamilyChatAPI.DbContexts.Read;
using FamilyChatAPI.DbContexts.Write;
using FamilyChatAPI.DTO;
using FamilyChatAPI.Dtos;
using FamilyChatAPI.Helper;
using FamilyChatAPI.IRepository;
using FamilyChatAPI.Models.Write;
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
        public FamilyChatRepository(ReadDbContext readDbContext, WriteDbContext writeDbContext, IJwtToken jwtToken)
        {
            _contextR = readDbContext;
            _contextW = writeDbContext;
            _jwtToken = jwtToken;
        }
        public async Task<UserDto> GetUserById(long id)
        {
            try
            {
                var data = await _contextR.TblUsers.Where(x => x.IntUserId == id && x.IsActive == true).Select( u =>
                    new UserDto
                    {
                        UserId = u.IntUserId,
                        UserName = u.StrUserName
                    }).FirstOrDefaultAsync();
                return data;
            }catch (Exception ex)
            {
                throw ex;
            }
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
                msg.message = "Welcome " + UserName;
                UserDto usr = new UserDto();

                if (data != null) {
                    usr.UserName = data.StrUserName;
                    usr.UserId = data.IntUserId;
                    msg.Token = _jwtToken.GenerateToken(usr);
                }
                else
                {
                    throw new Exception("Wrong Username and Password");
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
                        messageHelper.message = "Successfully Added";
                        messageHelper.statusCode = 200;
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
                    msg.message = "Faild to Save";
                }
                msg.statusCode = 200;
                return msg;

            }catch (Exception ex)
            {
                throw ex;
            }
        }

    }
}
