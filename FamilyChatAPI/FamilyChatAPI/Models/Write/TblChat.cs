using System;
using System.Collections.Generic;

namespace FamilyChatAPI.Models.Write
{
    public partial class TblChat
    {
        public byte IntChatId { get; set; }
        public string StrUserId { get; set; } = null!;
        public bool IsAcitve { get; set; }
    }
}
