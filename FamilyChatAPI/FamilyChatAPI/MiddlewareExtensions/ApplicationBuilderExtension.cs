using FamilyChatAPI.IRepository;

namespace FamilyChatAPI.MiddlewareExtensions
{
    public static class ApplicationBuilderExtension
    {
        public static void UseSqlTableDependency<T>(this IApplicationBuilder applicationBuilder, string connectionString)
            where T : ISubscribeNotificationTableDependency
        {
            var serviceProvider = applicationBuilder.ApplicationServices;
            var service = serviceProvider.GetService<T>();
            service?.SubscribeTableDependency(connectionString);
        }
    }
}
