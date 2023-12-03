using System;
using System.Collections.Generic;

namespace FamilyChatAPI.Models.Read
{
    public partial class TblMessage
    {
        public long IntMessageId { get; set; }
        public byte IntChatId { get; set; }
        public byte IntUserId { get; set; }
        public string StrMessage { get; set; } = null!;
        public bool IsActive { get; set; }
    }
}
