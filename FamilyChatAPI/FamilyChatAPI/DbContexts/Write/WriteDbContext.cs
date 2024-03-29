﻿using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata;
using FamilyChatAPI.Models.Write;

namespace FamilyChatAPI.DbContexts.Write
{
    public partial class WriteDbContext : DbContext
    {
        public WriteDbContext()
        {
        }

        public WriteDbContext(DbContextOptions<WriteDbContext> options)
            : base(options)
        {
        }

        public virtual DbSet<TblChat> TblChats { get; set; } = null!;
        public virtual DbSet<TblMessage> TblMessages { get; set; } = null!;
        public virtual DbSet<TblUser> TblUsers { get; set; } = null!;

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            if (!optionsBuilder.IsConfigured)
            {
#warning To protect potentially sensitive information in your connection string, you should move it out of source code. You can avoid scaffolding the connection string by using the Name= syntax to read it from configuration - see https://go.microsoft.com/fwlink/?linkid=2131148. For more guidance on storing connection strings, see http://go.microsoft.com/fwlink/?LinkId=723263.
                optionsBuilder.UseSqlServer("user id=iftekhar_SQLLogin_1;pwd=pyzxvfb2tz;data source=FamilyChatDB.mssql.somee.com;persist security info=False;initial catalog=FamilyChatDB");
            }
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<TblChat>(entity =>
            {
                entity.HasKey(e => e.IntChatId);

                entity.ToTable("tblChat");

                entity.Property(e => e.IntChatId)
                    .ValueGeneratedOnAdd()
                    .HasColumnName("intChatId");

                entity.Property(e => e.IntFromUserId).HasColumnName("intFromUserId");

                entity.Property(e => e.IntToUserId).HasColumnName("intToUserId");

                entity.Property(e => e.IsAcitve).HasColumnName("isAcitve");
            });

            modelBuilder.Entity<TblMessage>(entity =>
            {
                entity.HasKey(e => e.IntMessageId);

                entity.ToTable("tblMessage");

                entity.Property(e => e.IntMessageId).HasColumnName("intMessageId");

                entity.Property(e => e.DteMessageDateTime)
                    .HasColumnType("datetime")
                    .HasColumnName("dteMessageDateTime");

                entity.Property(e => e.IntChatId).HasColumnName("intChatId");

                entity.Property(e => e.IntUserId).HasColumnName("intUserId");

                entity.Property(e => e.IsActive).HasColumnName("isActive");

                entity.Property(e => e.StrMessage)
                    .HasMaxLength(50)
                    .HasColumnName("strMessage");
            });

            modelBuilder.Entity<TblUser>(entity =>
            {
                entity.HasKey(e => e.IntUserId);

                entity.ToTable("tblUser");

                entity.Property(e => e.IntUserId)
                    .ValueGeneratedOnAdd()
                    .HasColumnName("intUserId");

                entity.Property(e => e.IsActive).HasColumnName("isActive");

                entity.Property(e => e.StrConnectionId)
                    .HasMaxLength(100)
                    .HasColumnName("strConnectionId");

                entity.Property(e => e.StrPassword)
                    .HasMaxLength(8)
                    .HasColumnName("strPassword");

                entity.Property(e => e.StrUserName)
                    .HasMaxLength(10)
                    .HasColumnName("strUserName");
            });

            OnModelCreatingPartial(modelBuilder);
        }

        partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
    }
}
