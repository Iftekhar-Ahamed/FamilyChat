using System;
using System.Collections.Generic;

namespace FamilyChatAPI.Models.Write
{
    public partial class TblUser
    {
        public byte IntUserId { get; set; }
        public string StrUserName { get; set; } = null!;
        public string StrPassword { get; set; } = null!;
        public bool IsActive { get; set; }
        public string? StrConnectionId { get; set; }
    }
}
