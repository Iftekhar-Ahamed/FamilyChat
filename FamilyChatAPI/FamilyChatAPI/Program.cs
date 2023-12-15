using FamilyChatAPI.DbContexts.Read;
using FamilyChatAPI.DbContexts.Write;
using FamilyChatAPI.IRepository;
using FamilyChatAPI.Repository;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using System.Text;
using Microsoft.OpenApi.Models;
using Microsoft.Data.SqlClient;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers();
builder.Services.AddSignalR();


// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo { Title = "crud-api", Version = "v1" });
    c.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
    {
        Description = "JWT Authorization header using the Bearer scheme. Example: 'Bearer {token}'",
        Name = "Authorization",
        In = ParameterLocation.Header,
        Type = SecuritySchemeType.Http,
        BearerFormat = "JWT",
        Scheme = "bearer"
    });
    c.AddSecurityRequirement(new OpenApiSecurityRequirement
                {
                    {
                         new OpenApiSecurityScheme
                         {
                            Reference = new OpenApiReference
                            {

                                Type = ReferenceType.SecurityScheme,
                                Id = "Bearer"
                            }
                        },
                        new string[] {}
                    }
                });
});

//Dependency Injection
builder.Services.AddScoped<IFamilyChat, FamilyChatRepository>();
builder.Services.AddScoped<IJwtToken,JwtTokenRepository>();
builder.Services.AddScoped<ChatHub>();

//SignalR
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowSpecificOrigins",
        builder => builder
            .WithOrigins("http://FamilyChat.somee.com", "https://localhost:44320")
            .AllowAnyMethod()
            .AllowAnyHeader()
            .AllowCredentials());
});

string connection = builder.Configuration.GetConnectionString("MainConnection");
builder.Services.AddDbContext<ReadDbContext>(options => options.UseSqlServer(connection), ServiceLifetime.Transient);
builder.Services.AddDbContext<WriteDbContext>(options => options.UseSqlServer(connection), ServiceLifetime.Transient);



builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
.AddJwtBearer(options =>
{
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuer = true,
        ValidateAudience = true,
        ValidateIssuerSigningKey = true,
        ValidIssuer = builder.Configuration["Jwt:Issuer"], // Expected Issuer
        ValidAudience = builder.Configuration["Jwt:Audience"], // Expected Audience
        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(builder.Configuration["Jwt:Key"])) // Key to validate the signature
    };
});




var app = builder.Build();

app.UseCors("AllowSpecificOrigins");

app.MapHub<ChatHub>("/notificationHub");

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI(c =>
    {
        c.SwaggerEndpoint("/swagger/v1/swagger.json", "FamilyChatAPI V1");
    });
}

app.UseHttpsRedirection();

app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();

app.Run();
