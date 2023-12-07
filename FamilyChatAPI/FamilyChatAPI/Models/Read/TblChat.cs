using System;
using System.Collections.Generic;

namespace FamilyChatAPI.Models.Read
{
    public partial class TblChat
    {
        public byte IntChatId { get; set; }
        public byte IntFromUserId { get; set; }
        public byte IntToUserId { get; set; }
        public bool IsAcitve { get; set; }
    }
}
