package com.company.model;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataSources {
    public static final String DB_NAME = "music.db";

    public static Path path = FileSystems.getDefault().getPath("MusicServer\\" + DB_NAME);

    public static final String CONNECTION_STRING = "jdbc:sqlite:" + path.toAbsolutePath();

    public static final String TABLE_ARTISTS = "artists";
    public static final String COLUMN_ARTISTS_ID = "_id";
    public static final String COLUMN_ARTISTS_NAME = "name";
    public static final int INDEX_ARTISTS_ID = 1;
    public static final int INDEX_ARTISTS_NAME = 2;

    public static final String TABLE_ALBUMS = "albums";
    public static final String COLUMN_ALBUMS_ID = "_id";
    public static final String COLUMN_ALBUMS_NAME = "name";
    public static final String COLUMN_ALBUMS_ARTIST = "artist";
    public static final int INDEX_ALBUMS_ID = 1;
    public static final int INDEX_ALBUMS_NAME = 2;
    public static final int INDEX_ALBUMS_ARTIST = 3;

    public static final String TABLE_SONG = "songs";
    public static final String COLUMN_SONG_ID = "_id";
    public static final String COLUMN_SONG_TRACK = "track";
    public static final String COLUMN_SONG_TITLE = "title";
    public static final String COLUMN_SONG_ALBUM = "album";
    public static final int INDEX_SONG_ID = 1;
    public static final int INDEX_SONG_TRACK = 2;
    public static final int INDEX_SONG_TITLE = 3;
    public static final int INDEX_SONG_ALBUM = 4;

    public static final String TABLE_ARTIST_SONG_VIEW = "artist_list";

    public static final String QUERY_SONG_INFO = "SELECT " + COLUMN_ARTISTS_NAME + ", " + COLUMN_SONG_ALBUM + ", " +
            COLUMN_SONG_TRACK + " FROM " + TABLE_ARTIST_SONG_VIEW +" WHERE " + COLUMN_SONG_TITLE + " = ?";

    public static final String QUERY_ALBUMS_BY_ARTIST = "SELECT DISTINCT " + COLUMN_SONG_ALBUM + " FROM " + TABLE_ARTIST_SONG_VIEW +
            " WHERE " + COLUMN_ARTISTS_NAME + " = ?";

    //SELECT track,title FROM artist_list WHERE album = "Grace"

    public static final String QUERY_SONGS_FROM_ALBUM = "SELECT " + COLUMN_SONG_TRACK + ", " + COLUMN_SONG_TITLE + " FROM " +
            TABLE_ARTIST_SONG_VIEW + " WHERE " + COLUMN_SONG_ALBUM + " = ?";

    private PreparedStatement querySongsFromAlbum;
    private PreparedStatement queryAlbumsByArtist;
    private PreparedStatement querySongInfo;
    private Connection connection;

    public boolean open() {
        try{
            connection = DriverManager.getConnection(CONNECTION_STRING);
            querySongInfo = connection.prepareStatement(QUERY_SONG_INFO);
            queryAlbumsByArtist = connection.prepareStatement(QUERY_ALBUMS_BY_ARTIST);
            querySongsFromAlbum = connection.prepareStatement(QUERY_SONGS_FROM_ALBUM);

            return true;
        } catch (SQLException e){
            System.out.println("Error establishing connection: " + e.getMessage());
            return false;
        }
    }

    public void close(){
        try{
            if(querySongInfo != null)
                querySongInfo.close();

            if(queryAlbumsByArtist != null)
                queryAlbumsByArtist.close();

            if(querySongsFromAlbum != null)
                querySongsFromAlbum.close();

            if(connection!=null)
                connection.close();

        } catch (SQLException e){
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    public List<SongArtist> getQuerySongInfo(String title){
        try{
            querySongInfo.setString(1,title);
            ResultSet results = querySongInfo.executeQuery();
            System.out.println(querySongInfo.toString());

            List<SongArtist> songArtistList = new ArrayList<>();

            while(results.next()){
                SongArtist songArtist = new SongArtist();
                songArtist.setArtist(results.getString(1));
                songArtist.setAlbum(results.getString(2));
                songArtist.setTrack(results.getInt(3));
                songArtistList.add(songArtist);
            }
            return songArtistList;

        } catch (SQLException e){
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
    }

    public List<String> getQueryAlbumsByArtist(String artistName){
        try{
            queryAlbumsByArtist.setString(1,artistName);
            ResultSet results = queryAlbumsByArtist.executeQuery();

            List<String> albums = new ArrayList<>();

            while(results.next()){
                albums.add(results.getString(1));
            }
            return albums;

        } catch (SQLException e){
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
    }

    public List<AlbumSongs> getQuerySongsFromAlbum(String albumName){
        try {
            querySongsFromAlbum.setString(1,albumName);
            ResultSet results = querySongsFromAlbum.executeQuery();

            List<AlbumSongs> albumSongsList = new ArrayList<>();

            while (results.next()){
                AlbumSongs albumSongs = new AlbumSongs();
                albumSongs.setTrack(results.getInt(1));
                albumSongs.setTitle(results.getString(2));
                albumSongsList.add(albumSongs);
            }
            return albumSongsList;
        } catch (SQLException e){
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
    }
}
