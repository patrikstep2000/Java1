create database Java
go

use Java
go

--Tables
create table AppUserRole(
	ID int primary key identity,
	Name nvarchar(100) not null
)

create table AppUser(
	ID int primary key identity,
	Username nvarchar(100) not null,
	Password nvarchar(100) not null,
	RoleID int not null,

	foreign key (RoleID) references AppUserRole(ID)
)

create table Actor(
	ID int primary key identity,
	Name nvarchar(100) not null
)

create table Director(
	ID int primary key identity,
	Name nvarchar(100) not null
)

create table Movie(
	ID int primary key identity,
	Title nvarchar(300) null,
	Link nvarchar(300) null,
	OriginalTitle nvarchar(300) null,
	PublishedDate nvarchar(90) null,
	Genre nvarchar(300) null,
	PicturePath nvarchar(300) null,
	Length int null
)

create table MovieDirector(
	ID int primary key identity,
	MovieID int not null,
	DirectorID int not null,

	foreign key (MovieID) references Movie(ID),
	foreign key (DirectorID) references Director(ID)
)

create table MovieActor(
	ID int primary key identity,
	MovieID int not null,
	ActorID int not null,

	foreign key (MovieID) references Movie(ID),
	foreign key (ActorID) references Actor(ID)
)
go

--Insert admin and user
insert into AppUserRole values ('Administrator'), ('User')
insert into AppUser values ('admin', 'admin', 1), ('user', 'user', 2)
go

--Movie CRUD
create proc dbo.createMovie
	@Title nvarchar(300),
	@Link nvarchar(300),
	@OriginalTitle nvarchar(300),
	@PublishedDate nvarchar(90),
	@Genre nvarchar(90),
	@PicturePath nvarchar(300),
	@Length int,
	@ID int output
as
	if not exists (select * from Movie where Title=@Title)
			begin
				insert into Movie values (@Title, @Link, @OriginalTitle, @PublishedDate, @Genre, @PicturePath, @Length)
				set @ID = SCOPE_IDENTITY()
			end
		else
			begin
				set @ID = (select ID from Movie where Title=@Title)
			end
go

create proc dbo.updateMovie
	@Title nvarchar(300),
	@Link nvarchar(300),
	@OriginalTitle nvarchar(300),
	@PublishedDate nvarchar(90),
	@Genre nvarchar(90),
	@PicturePath nvarchar(300),
	@Length int,
	@ID int
as
	begin
		update Movie set
		Title=@Title,
		Link=@Link,
		OriginalTitle=@OriginalTitle,
		PublishedDate=@PublishedDate,
		Genre=@Genre,
		PicturePath=@PicturePath,
		Length=@Length
		where
		ID=@ID
	end
go


create proc dbo.deleteMovie
	@ID int
as
	begin
		delete from MovieActor where MovieID=@ID
		delete from MovieDirector where MovieID=@ID
		delete from Movie where ID=@ID
	end
go


create proc dbo.selectMovie
	@ID int
as
	begin
		select * from Movie where ID=@ID
	end
go


create proc dbo.selectMovies
as
	begin
		select * from Movie
	end
go


--Actor CRUD
create proc dbo.createActor
	@Name nvarchar(300),
	@ID int output
as
	begin
		declare @temp int
		select @temp=ID from Actor where Name=@Name
		if exists (select ID from Actor where Name=@Name)
			begin
				set @ID=@temp
			end
		else
			begin
				insert into Actor values (@Name)
				set @ID = SCOPE_IDENTITY()
			end
	end
go


create proc dbo.updateActor
	@Name nvarchar(300),
	@ID int
as
	begin
		update Actor
		set Name=@Name
		where ID=@ID
	end
go

create proc dbo.deleteActor
	@ID int
as 
	begin
		delete from MovieActor where ActorID=@ID
		delete from Actor where ID=@ID
	end

create proc dbo.selectActor
	@ID int
as
	begin
		select * from Actor where ID=@ID
	end
go

create proc dbo.selectActors
as
	begin
		select * from Actor
	end
go


--Director CRUD
create proc dbo.createDirector
	@Name nvarchar(300),
	@ID int output
as
	begin
		declare @temp int
		select @temp=ID from Director where Name=@Name
		if exists (select ID from Director where Name=@Name)
			begin
				set @ID=@temp
			end
		else
			begin
				insert into Director values (@Name)
				set @ID = SCOPE_IDENTITY()
			end
	end
go


create proc dbo.updateDirector
	@Name nvarchar(300),
	@ID int
as
	begin
		update Director
		set Name=@Name
		where ID=@ID
	end
go

create proc dbo.deleteDirector
	@ID int
as 
	begin
		delete from MovieDirector where DirectorID=@ID
		delete from Director where ID=@ID
	end

create proc dbo.selectDirector
	@ID int
as
	begin
		select * from Director where ID=@ID
	end
go

create proc dbo.selectDirectors
as
	begin
		select * from Director
	end
go


--MovieActor and MovieDirector procs
create proc dbo.createMovieDirector
	@MovieID int,
	@DirectorID int
as
	begin
		if not exists (select * from MovieDirector where MovieID=@MovieID and DirectorID=@DirectorID)
			begin
				insert into MovieDirector values (@MovieID, @DirectorID)
			end
	end
go


create proc dbo.createMovieActor
	@MovieID int,
	@ActorID int
as
	begin
		if not exists (select * from MovieActor where MovieID=@MovieID and ActorID=@ActorID)
			begin
				insert into MovieActor values (@MovieID, @ActorID)
			end
	end
go

create proc dbo.deleteMovieDirector
	@MovieID int,
	@DirectorID int
as
	begin
		delete from MovieDirector where MovieID=@MovieID and DirectorID=@DirectorID
	end
go


create proc dbo.deleteMovieActor
	@MovieID int,
	@ActorID int
as
	begin
		delete from MovieActor where MovieID=@MovieID and ActorID=@ActorID
	end
go


create proc dbo.selectDirectorsByMovieId
	@MovieID int
as
	begin
		select d.* from MovieDirector ma
		join Director d on d.ID=ma.DirectorID
		where ma.MovieID=@MovieID
	end
go


create proc dbo.selectActorsByMovieId
	@MovieID int
as
	begin
		select a.* from MovieActor ma
		join Actor a on a.ID=ma.ActorID
		where ma.MovieID=@MovieID
	end
go


--User
create proc dbo.authUser
	@Username nvarchar(300),
	@Password nvarchar(300),
	@Role nvarchar(100) out
as
	begin
		select @Role=Name from AppUserRole where ID=(select RoleID from AppUser where Username=@Username and Password=@Password)
	end
go

--Delete all
create proc dbo.deleteMovies
as
begin
	delete from MovieActor
	delete from MovieDirector
	delete from Actor
	delete from Director
	delete from Movie
end
go
