using System.Runtime.Serialization;

namespace FamilyChatAPI.Helper
{
    public class MessageHelper
    {
        [DataMember]
        public string Message {  get; set; }
        public int StatusCode { get; set; }
        public string Token { get; set; }
        public long UserId { get; set; }
    }
}
