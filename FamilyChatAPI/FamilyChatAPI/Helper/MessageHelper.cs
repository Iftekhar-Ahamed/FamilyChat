using System.Runtime.Serialization;

namespace FamilyChatAPI.Helper
{
    public class MessageHelper
    {
        [DataMember]
        public string message {  get; set; }
        public int statusCode { get; set; }
    }
}
