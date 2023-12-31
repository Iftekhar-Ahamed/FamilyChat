﻿using FamilyChatAPI.DTO;
using FamilyChatAPI.Dtos;
using FamilyChatAPI.Helper;
using Microsoft.AspNetCore.Mvc;
using System.Threading.Tasks;

namespace FamilyChatAPI.IRepository
{
    public interface IFamilyChat
    {
        Task<MessageHelper> CreateUser(CreateUserDto UserInfotmation);
        Task<UserDto> GetUserById(long UserId);
        Task<MessageHelper> UserLogIn(string UserName, string Password);
        Task<MessageHelper> CreateNewConnection(long from, long to);
        Task<List<ConnectionListDto>> GetAllConnectionByUserId(long id);
        Task<MessageHelper> SaveMessageByChatId(List<SaveMessageDto> msgList);
        Task<AllMessageByChatIdDto> GetAllMessageByChatId(long ChatId, long ChatFriendId);
    }
}
