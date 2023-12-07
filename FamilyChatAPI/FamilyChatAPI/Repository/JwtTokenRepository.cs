using FamilyChatAPI.Helper;
using FamilyChatAPI.IRepository;
using FamilyChatAPI.DTO;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using FamilyChatAPI.DbContexts;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Security.Claims;
using Microsoft.Extensions.Options;
using System.Security.Principal;
using System.Text;

namespace FamilyChatAPI.Repository
{
    public class JwtTokenRepository:IJwtToken
    {
        IConfiguration _configuration;
        public JwtTokenRepository(IConfiguration configuration)
        {
            _configuration = configuration;
        }
        
        public string GenerateToken(UserDto _userData)
        {
            var claims = new[] {
                        new Claim("UserId", _userData.UserId.ToString()),
                        new Claim("UserName",_userData.UserName)};


            var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_configuration["Jwt:Key"]));
            var signIn = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);
            var token = new JwtSecurityToken(
                audience: _configuration["Jwt:Audience"],
                issuer : _configuration["Jwt:Issuer"],
                claims : claims,
                expires: DateTime.UtcNow.AddMinutes(200),
                signingCredentials: signIn);
            return new JwtSecurityTokenHandler().WriteToken(token);
        }
        public bool CheckTimeExpire(ClaimsIdentity identity)
        {
            if (identity != null)
            {
                // Find the 'exp' claim (expiration time)
                var expirationClaim = identity.FindFirst("exp");

                if (expirationClaim != null && long.TryParse(expirationClaim.Value, out var expirationTime))
                {
                    // Convert the Unix timestamp to a DateTime
                    var expirationDateTime = DateTimeOffset.FromUnixTimeSeconds(expirationTime).DateTime;

                    // Get the current time
                    var currentTime = DateTime.UtcNow;

                    if (currentTime < expirationDateTime)
                    {
                        // JWT is still valid
                        return true;
                    }
                    else
                    {
                        // JWT has expired
                        return false;
                    }
                }
                else
                {
                    return false;
                }
            }

            return false;
        }
    }

}
