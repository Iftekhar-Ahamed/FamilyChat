using FamilyChatAPI.CacheData;
using FamilyChatAPI.DbContexts.Write;
using FamilyChatAPI.Models.Write;
using FamilyChatAPI.Repository;
using Microsoft.Extensions.Caching.Memory;

public class CacheBackup : BackgroundService
{
    private ILastMessageCache _lastMessageCache;
    private readonly IServiceProvider _serviceProvider;

    public CacheBackup(IServiceProvider serviceProvider)
    {
        _serviceProvider = serviceProvider;
    }
    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        while (!stoppingToken.IsCancellationRequested)
        {
            using (var scope = _serviceProvider.CreateScope())
            {
                var _lastMessageCache = scope.ServiceProvider.GetRequiredService<ILastMessageCache>();
                await _lastMessageCache.BackupCacheToDatabase();

            }
            await Task.Delay(TimeSpan.FromMinutes(10), stoppingToken);
        }
    }
    
}

