using System;
using System.Collections.Generic;

namespace FamilyChatAPI.Models.Write
{
    public partial class TblMessage
    {
        public long IntMessageId { get; set; }
        public byte IntChatId { get; set; }
        public byte IntUserId { get; set; }
        public string StrMessage { get; set; } = null!;
        public DateTime DteMessageDateTime { get; set; }
        public bool IsActive { get; set; }
    }
}
