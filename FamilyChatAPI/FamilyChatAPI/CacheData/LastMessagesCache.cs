using FamilyChatAPI.DbContexts.Write;
using FamilyChatAPI.Dtos;
using FamilyChatAPI.Dtos.ChatHubDto;
using FamilyChatAPI.IRepository;
using FamilyChatAPI.Models.Write;
using Microsoft.Extensions.DependencyInjection;
using Newtonsoft.Json;
using TableDependency.SqlClient.Base.Messages;

namespace FamilyChatAPI.CacheData
{
    public class LastMessagesCache : ILastMessageCache
    {
        private Dictionary<long, List<TblMessage>> _lastTblMessages;
        private string fileName = "SavedLastMessagesList.txt";
        public LastMessagesCache() {
            try
            {
                string s = File.ReadAllLines(fileName).ToString();
                if (s != "")
                {
                    _lastTblMessages = JsonConvert.DeserializeObject<Dictionary<long, List<TblMessage>>>(s);
                }
            } catch (Exception e)
            {
                _lastTblMessages = new Dictionary<long, List<TblMessage>>();
            }
        }
        
        ~LastMessagesCache() {
            using (TextWriter tw = new StreamWriter(fileName))
            {
                string s = JsonConvert.SerializeObject(_lastTblMessages);
                tw.WriteLine(s);
            }
        }
        public List<ChatMessageDto> GetMessageByChatId(long chatId)
        {
            List<ChatMessageDto> result = new List<ChatMessageDto>();
            if (_lastTblMessages.ContainsKey(chatId))
            {
                result = _lastTblMessages[chatId].Where(m => m.IsActive == true).Select(m => new ChatMessageDto
                {
                    messageId = m.IntMessageId,
                    messageText = m.StrMessage,
                    chatId = m.IntChatId,
                    UserId = m.IntUserId,
                    messageDateTime = m.DteMessageDateTime
                }).OrderByDescending(m => m.messageId).Take(15).ToList();
                if (result.Count == 0)
                {
                    result.Add(new ChatMessageDto { messageText = "" });
                }
                return result;
            }
            return result;
        }
        public long AddMessageByChatId(byte ChatId, byte IntUserId, string StrMessage, DateTime dateTimeMsg)
        {
            if (_lastTblMessages.ContainsKey(ChatId) == false)
            {
                _lastTblMessages.Add(ChatId, new List<TblMessage>());
            }
            var id = _lastTblMessages[ChatId].Count();
            if (id == 0)
            {
                _lastTblMessages[ChatId] = new List<TblMessage>();
            }

            _lastTblMessages[ChatId].Add(new TblMessage
            {
                IntMessageId = id,
                IntChatId = ChatId,
                IntUserId = IntUserId,
                StrMessage = StrMessage,
                DteMessageDateTime = dateTimeMsg,
                IsActive = true
            });
            return _lastTblMessages[ChatId].Count();
        }
        public bool DeleteMessageByChatId(long ChatId)
        {
            _lastTblMessages[ChatId] = new List<TblMessage>();
            return true;
        }
    }
}
