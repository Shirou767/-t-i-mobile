package com.example.ghiblit.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Data extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ghilblit.db";
    public static final int DATABASE_VERSION = 1;

    public Data(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Bảng User
        db.execSQL("CREATE TABLE IF NOT EXISTS User (" +
                "UID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Name TEXT NOT NULL," +
                "DateOfBirth TEXT," +
                "Phone TEXT," +
                "Username TEXT UNIQUE NOT NULL," +
                "Password TEXT NOT NULL)");

        // Bảng Theater
        db.execSQL("CREATE TABLE IF NOT EXISTS Theater (" +
                "TID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Name TEXT NOT NULL," +
                "Address TEXT," +
                "Phone TEXT," +
                "PTicket TEXT," +    // Giá vé cơ bản
                "Seat TEXT," +       // Danh sách ghế hoặc sơ đồ
                "Pseat,"+
                "Image TEXT" +       // Tên file ảnh
                ");");


        // Bảng Film
        db.execSQL("CREATE TABLE IF NOT EXISTS Film (" +
                "FID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Title TEXT NOT NULL," +
                "Genre TEXT," +
                "Description TEXT," +
                "Duration INTEGER," +
                "Image TEXT," +
                "BG TEXT," +
                "TID INTEGER," +
                "FOREIGN KEY (TID) REFERENCES Theater(TID))");

        //Bảng Booking
        db.execSQL("CREATE TABLE IF NOT EXISTS Booking (" +
                "BID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "UID INTEGER NOT NULL," +
                "BookingDate TEXT," +
                "Theater TEXT," +      // Tên rạp (có thể thay bằng TID nếu muốn khóa ngoại chặt hơn)
                "TotalPrice REAL," +
                "Status TEXT," +
                "FOREIGN KEY (UID) REFERENCES User(UID)" +
                ");");


        // Bảng BookDetail
        db.execSQL("CREATE TABLE IF NOT EXISTS BookDetail (" +
                "BID INTEGER NOT NULL," +
                "FID INTEGER NOT NULL," +
                "TID INTEGER NOT NULL," +
                "Seat TEXT," +
                "Price REAL," +
                "PRIMARY KEY (BID, FID, Seat)," +
                "FOREIGN KEY (BID) REFERENCES Booking(BID)," +
                "FOREIGN KEY (FID) REFERENCES Film(FID)," +
                "FOREIGN KEY (TID) REFERENCES Theater(TID)" +
                ");");
        // Bảng ShowTime
        db.execSQL("CREATE TABLE IF NOT EXISTS Showtime (" +
                "SID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "FID INTEGER NOT NULL," +
                "TID INTEGER NOT NULL," +
                "ShowTime TEXT NOT NULL," +
                "FOREIGN KEY (FID) REFERENCES Film(FID)," +
                "FOREIGN KEY (TID) REFERENCES Theater(TID)" +
                ");");
    }

    public void addSampleData() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Thêm user
        db.execSQL("INSERT INTO User (Name, DateOfBirth, Phone, Username, Password) " +
                "VALUES ('Totoro', '20/07/2001', '0988888888', 'totoro', '123')");


        // Thêm Rạp
        db.execSQL("INSERT INTO Theater (Name, Address, Phone, PTicket, Seat, Pseat, Image) " +
                "VALUES ('Ghibli Cinema', 'Tokyo, Japan', '0123456789', '100000', 'A1,A2,A3,B1,B2,B3,C1,C2', 'A:30000,B:20000,C:10000', 'ghiblit1.png')");

        db.execSQL("INSERT INTO Theater (Name, Address, Phone, PTicket, Seat, Pseat, Image) " +
                "VALUES ('Ghibli Theater', 'Osaka, Japan', '0987654321', '95000', 'A1,A2,B1,B2,C1,C2', 'A:30000,B:20000,C:10000', 'ghiblit2.png')");

        db.execSQL("INSERT INTO Theater (Name, Address, Phone, PTicket, Seat, Pseat, Image) " +
                "VALUES ('Ghibli Home', 'Shibuya, Japan', '0986485378', '90000', 'A1,A2,B1,C1,C2', 'A:30000,B:20000,C:10000', 'ghiblit3.png')");

        db.execSQL("INSERT INTO Theater (Name, Address, Phone, PTicket, Seat, Pseat, Image) " +
                "VALUES ('Ghibli Memory', 'Hokkaidō, Japan', '0985544356', '85000', 'A1,B1,B2,C1', 'A:30000,B:20000,C:10000', 'ghiblit4.png')");

        db.execSQL("INSERT INTO Theater (Name, Address, Phone, PTicket, Seat, Pseat, Image) " +
                "VALUES ('Ghibli Film', 'Fukushima, Japan', '0988854698', '88000', 'A1,A2,B2,C2', 'A:30000,B:20000,C:10000', 'ghiblit5.png')");


        // Thêm phim
        db.execSQL("INSERT INTO Film (Title, Genre, Description, Duration, Image, BG, TID) " +
                "VALUES ('Spirited Away', 'Phim gia đình, Nhật, Phim kinh điển, Anime giả tưởng, Phim anime,', 'chuyện kể về chuyến phiêu lưu của cô bé loài người Chihiro Ogino đến vùng đất linh hồn ma thuật trong lần chuyển nhà cùng bố mẹ. Do vô tình ăn phải thức ăn của thần linh nên cả bố và mẹ cô bị biến thành heo. Để giải cứu bố mẹ và quay trở về thế giới loài người, cô bé Chihiro Ogino phải làm việc cho mụ phù thủy Yubaba xảo quyệt. Ở thế giới này, Chihiro Ogino đã gặp lại Haku - vị thần đã cứu cô thoát chết lúc cô bị ngã xuống sông khi còn bé.', 125, 'spirited_away.png', 'spirited-bg.png', 1)");

        db.execSQL("INSERT INTO Film (Title, Genre, Description, Duration, Image, BG, TID) " +
                "VALUES ('My Neighbor Totoro', 'Phim gia đình, Phim trẻ em & gia đình, Nhật, Phim kinh điển, Anime giả tưởng, Phim anime', 'Bộ phim đã kể một câu chuyện nhân văn về cách những đứa trẻ khám phá thế giới thông qua trí tưởng thượng phong phú từ chúng. Câu chuyện bắt đầu khi gia đình hai chị em Satsuki và Mei Kusakabe chuyển đến sinh sống tại một vùng nông thôn thanh bình. Trong một lần dạo chơi trong khu rừng gần nhà, Mei tình cờ gặp gỡ Totoro, một con thú bụng bự dễ thương và được mệnh danh là thần rừng nơi đây. Từ đó, cuộc phiêu lưu kỳ diệu của hai cô gái nhỏ và Totoro chính thức bắt đầu.', 86, 'totoro.png', 'totoro-bg.png', 1)");

        db.execSQL("INSERT INTO Film (Title, Genre, Description, Duration, Image, BG, TID) " +
                "VALUES ('Howls Moving Castle', 'Phim gia đình, Phim trẻ em & gia đình, Nhật, Phim chuyển thể từ sách, Anime giả tưởng, Phim anime', 'Sophie, Phim kể về Sophie - một cô gái trẻ tình cờ bị phù phép thành một bà lão. Sophie buộc phải rời bỏ gia đình, đi tìm cách hóa giải lời nguyền. Trên đường đi, Sophie gặp chàng pháp sư Howl, con quỷ lửa cùng tòa lâu đài bay kỳ lạ, từ đó, họ bắt đầu những chuyến phiêu lưu đầy phép thuật', 119, 'howl.png', 'howl-bg.png', 2)");

        db.execSQL("INSERT INTO Film (Title, Genre, Description, Duration, Image, BG, TID) " +
                "VALUES ('Ponyo', 'Phim gia đình, Phim trẻ em & gia đình, Nhật, Phim anime', 'Bộ phim nói về một cô bé cá và chàng bé trai nhỏ gặp nhau và trở thành bạn thân. Cô bé cá tên là Ponyo, con của một vị thủy tướng biển và chàng bé trai tên là Sosuke. Cả hai đã trải qua nhiều cuộc phiêu lưu để đến với nhau. Bộ phim mang đến cho khán giả một câu chuyện tình bạn đáng yêu giữa hai đứa trẻ, cũng như những thông điệp về tình yêu, sự hiểu biết và sự hy sinh.', 100, 'ponyo.png', 'ponyo-bg.png', 2)");

        db.execSQL("INSERT INTO Film (Title, Genre, Description, Duration, Image, BG, TID) " +
                "VALUES ('Kiki’s Delivery Service', 'Phim gia đình, Phim trẻ em & gia đình, Nhật, Phim kinh điển, Phim chuyển thể từ sách, Anime giả tưởng, Phim anime', 'Truyện phim xoay quanh một nữ phù thủy nhỏ tuổi tên là Kiki. Cô bé chuyển đến sống ở một thành phố mới và sử dụng năng lực bay trên không của mình để kiếm sống. Theo Miyazaki, bộ phim đặc biệt miêu tả hố sâu ngăn cách giữa tính tự lập và sự phụ thuộc của các cô bé tuổi mới lớn ở Nhật Bản', 102, 'kiki.png', 'kiki-bg.png', 3)");

        db.execSQL("INSERT INTO Film (Title, Genre, Description, Duration, Image, BG, TID) " +
                "VALUES ('Castle in the Sky ', 'Phim gia đình, Phim trẻ em & gia đình, Nhật, Phim kinh điển, Anime giả tưởng, Phim anime', 'Được lấy cảm hứng từ hòn đảo bay Laputa trong tiểu thuyết Gulliver Du Ký của nhà văn Jonathan Swift, phim kể về chuyến phiêu lưu thú vị và không kém chông gai của cô bé Sheeta và cậu bé thợ mỏ Pazu trên hành trình tìm kiếm lâu đài Laputa. Trên đường đi tìm lâu đài, Sheeta và Pazu phải vượt qua nhiều thử thách, và phải thoát khỏi sự rượt đuổi của ác nhân Muska.', 124, 'laputa.png', 'laputa-bg.png', 3)");

        db.execSQL("INSERT INTO Film (Title, Genre, Description, Duration, Image, BG, TID) " +
                "VALUES ('Grave of the Fireflies', 'Nhật, Anime lịch sử, Phim kinh điển, Phim chuyển thể từ sách, Anime chính kịch, Phim anime', 'Mộ Đom Đóm lấy bối cảnh trong giai đoạn hậu Chiến tranh Thế giới thứ hai ở xứ sở mặt trời mọc. Nội dung phim kể lại câu chuyện đầy nước mắt về tình cảm anh em thân thiết của hai đứa trẻ mồ côi tên là Seita và Setsuko.', 89, 'firefly.png', 'firefly-bg.png', 4)");

        db.execSQL("INSERT INTO Film (Title, Genre, Description, Duration, Image, BG, TID) " +
                "VALUES ('Whisper of the Heart', 'Chính kịch, Gia Đình', 'Whisper of the Heart là một bộ phim đầy bình dị, kể về những con người nhỏ bé - những người trẻ tuổi đang theo đuổi con đường nghệ thuật, cùng với những mơ ước bình dị đáng yêu.', 100, 'heart.png', 'heart.png', 2)");

        db.execSQL("INSERT INTO Film (Title, Genre, Description, Duration, Image, BG, TID) " +
                "VALUES ('Princess Mononoke', 'Phim gia đình, Anime hành động, Nhật, Phim kinh điển, Anime giả tưởng, Phim hành động & phiêu lưu, Phim anime', 'Trong cuộc chiến thảm khốc giữa con người và thiên nhiên, Princess Mononoke kể về chuyện tình phi thường, trong sáng và đẹp đẽ giữa chàng hoàng tử tộc Emishi can đảm, cương nghị - Ashitaka cùng Công chúa mạnh mẽ, quyết đoán của bộ tộc sói Mononoke - San. Chính Ashitaka đã giúp đỡ, bảo vệ San và kìm hãm sự hận thù giữa thiên nhiên và thế giới loài người.', 133, 'momonoke.png', 'momonoke-bg.png', 4)");

        db.execSQL("INSERT INTO Film (Title, Genre, Description, Duration, Image, BG, TID) " +
                "VALUES ('The Wind Rises', 'phim Chiến Tranh, phim Chính kịch, phim Lịch Sử, phim Tình Cảm,', 'Bộ phim hoạt hình kể về câu chuyện hư cấu của kỹ sư hàng không Nhật Bản Jiro Horikoshi, người đã thiết kế máy bay chiến đấu \"Zero\". Bộ phim theo dõi sự nghiệp của Jiro từ thời còn là sinh viên Đại học Tokyo đến khi ông phát triển chiếc Mitsubishi Ka-14. Bộ phim cũng khám phá tình yêu và những khó khăn mà Jiro phải đối mặt trong cuộc sống cá nhân và sự nghiệp của mình. ', 126, 'wind.png', 'wind-bg.png', 4)");

        db.execSQL("INSERT INTO Film (Title, Genre, Description, Duration, Image, BG, TID) " +
                "VALUES ('From Up On Poppy Hill ', 'Phim gia đình, Phim trẻ em & gia đình, Nhật, Anime lịch sử, Anime chính kịch, Phim anime, Anime về trường học', 'Bộ phim kể về câu chuyện của một cô gái trẻ tên là Umi Matsuzaki, sống tại một ngôi nhà trên đồi và cô đã phải đối mặt với những thách thức của tuổi trẻ cùng những bí ẩn về quá khứ của gia đình cô.\n" +
                "Cốt truyện của bộ phim xoay quanh việc Umi Matsuzaki phát hiện ra một bí mật về quá khứ của cha mình thông qua làm việc với một nhóm sinh viên trường cao đẳng. Trải qua những gian nan và khó khăn, Umi đã dần dần hiểu ra những điều bí ẩn và tìm thấy sự đồng cảm, tình yêu thương từ những người xung quanh.\n', 91, 'poppy.png', 'spirited-bg.png', 5)");

        db.execSQL("INSERT INTO Film (Title, Genre, Description, Duration, Image, BG, TID) " +
                "VALUES ('The Secret World Of Arrietty ', 'Phim gia đình, Phim trẻ em & gia đình, Nhật, Phim chuyển thể từ sách, Anime giả tưởng, Phim anime', 'Phim kể về cuộc phiêu lưu của Arrietty, một cô bé nhỏ sống trong một thế giới bí mật dưới lòng đất cùng với gia đình của mình. Cốt truyện của bộ phim xoay quanh cuộc sống bí mật và phiêu lưu của Arrietty khi cô bé phải đối mặt với những thách thức và nguy hiểm từ thế giới bên ngoài.', 94, 'arriety.png', 'arriety-bg.png', 5)");

        db.execSQL("INSERT INTO Film (Title, Genre, Description, Duration, Image, BG, TID) " +
                "VALUES ('The cat returns', 'Phim gia đình, Phim trẻ em & gia đình, Nhật, Anime giả tưởng, Phim anime', 'một nữ sinh trung học sau khi cô cứu được một chú mèo lạ khỏi bị xe tông, cô đã nhận được sự cảm kích của những chú mèo và sau đó cô được đưa đến vương quốc của loài mèo để trở thành vợ của thái tử. Cuộc phiêu lưu của Haru bắt đầu.', 75, 'cat.png', 'cat-bg.png', 1)");


        // Thêm booking
        db.execSQL("INSERT INTO Booking (UID, BookingDate, Theater, TotalPrice, Status) " +
                "VALUES (1, '2025-06-22', 'Ghibli Cinema', 100000, 'Confirmed')");

        // Thêm chi tiết vé
        db.execSQL("INSERT INTO BookDetail (BID, FID, TID, Seat, Price) " +
                "VALUES (1, 1, 1, 'A1', 100000)");

        // Giờ chiếu phim 1 tại rạp 1
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (1, 1, '10:00')");
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (1, 1, '14:00')");

// Phim 2 tại rạp 2
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (2, 2, '12:30')");
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (2, 2, '17:00')");

// Phim 3 tại rạp 3
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (3, 3, '11:00')");
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (3, 3, '16:00')");

// Phim 4 tại rạp 4
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (4, 4, '09:45')");
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (4, 4, '13:30')");

// Phim 5 tại rạp 5
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (5, 5, '15:15')");
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (5, 5, '18:45')");

// Phim 6 tại rạp 1
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (6, 1, '08:30')");
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (6, 1, '12:15')");

// Phim 7 tại rạp 2
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (7, 2, '10:45')");
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (7, 2, '13:00')");

// Phim 8 tại rạp 3
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (8, 3, '14:30')");
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (8, 3, '18:00')");

// Phim 9 tại rạp 4
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (9, 4, '11:45')");
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (9, 4, '16:15')");

// Phim 10 tại rạp 5
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (10, 5, '09:30')");
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (10, 5, '13:45')");

// Phim 11 tại rạp 1
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (11, 1, '15:30')");
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (11, 1, '19:15')");

// Phim 12 tại rạp 2
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (12, 2, '16:45')");
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (12, 2, '20:30')");

// Phim 13 tại rạp 3
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (13, 3, '17:30')");
        db.execSQL("INSERT INTO Showtime (FID, TID, ShowTime) VALUES (13, 3, '21:00')");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS BookDetail");
        db.execSQL("DROP TABLE IF EXISTS Booking");
        db.execSQL("DROP TABLE IF EXISTS Film");
        db.execSQL("DROP TABLE IF EXISTS Theater");
        db.execSQL("DROP TABLE IF EXISTS User");
        onCreate(db);
    }
}
