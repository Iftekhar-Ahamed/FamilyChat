using FamilyChatAPI.DTO;
using FamilyChatAPI.Helper;
using System;
using System.Security.Claims;
using System.Threading.Tasks;

namespace FamilyChatAPI.IRepository
{
    public interface IJwtToken
    {
        string GenerateToken(UserDto user);
        bool CheckTimeExpire(ClaimsIdentity identity);
    }
}
