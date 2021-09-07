package com.company;

import com.company.model.AlbumSongs;
import com.company.model.DataSources;
import com.company.model.SongArtist;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class Sender extends Thread{
    private Socket socket;

    public Sender(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try{
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(),true);
            DataSources dataSources = new DataSources();
            if(!dataSources.open()){
                System.out.println("Can't open the datasource");
                return;
            }

            while (true) {
                String receivedInput = input.readLine();
                System.out.println("Received Client Input: " + receivedInput);

                if (receivedInput.equalsIgnoreCase("exit")){
                    System.out.println("Exiting from Server");
                    break;
                } else {
                    switch (receivedInput){
                        case "QuerySong" :
                            String songName = input.readLine();
                            System.out.println("Received Song Name: " + songName);

                            List<SongArtist> songArtists = dataSources.getQuerySongInfo(songName);

                            if(songArtists.isEmpty()){
                                System.out.println("Reached here");
                                output.println("No Song Found" + "\n" +
                                        "\0");
                            } else {
                                String start = songName + " Info \n";
                                StringBuilder stringBuilder = new StringBuilder();
                                for (SongArtist songArtist: songArtists){
                                    String stringToReturn =
                                            "\t Artist: " + songArtist.getArtist() + "\n" +
                                            "\t Album: " + songArtist.getAlbum() + "\n" +
                                            "\t Track: " + songArtist.getTrack() + "\n";
                                    stringBuilder.append(stringToReturn);
                                }
                                System.out.println(start + stringBuilder.toString());
                                output.println(start + stringBuilder.toString() + "\0");
                            }
                            break;
                        case "QueryArtistAlbum" :
                            String artistName = input.readLine();
                            System.out.println("Received Artist Name: " + artistName);

                            List<String> albums = dataSources.getQueryAlbumsByArtist(artistName);

                            if(albums.isEmpty()){
                                output.println("No Songs By Artist found" + "\n" +
                                        "\0");
                            } else {
                                String start = "Albums by " + artistName + "\n";
                                StringBuilder stringBuilder = new StringBuilder();
                                for(String album : albums){
                                    String stringToReturn = "\t : " + album + "\n";
                                    stringBuilder.append(stringToReturn);
                                }
                                System.out.println(stringBuilder.toString());
                                output.println(start + stringBuilder.toString() + "\0");
                            }
                            break;
                        case "QueryAlbumSongs" :
                            String albumName = input.readLine();
                            System.out.println("Received Album Name: " + albumName);

                            List<AlbumSongs> albumSongsList = dataSources.getQuerySongsFromAlbum(albumName);

                            if(albumSongsList.isEmpty()){
                                output.println("Album not found..." + "\n" +
                                        "\0");
                            } else {
                                String start = "Songs in Album: " + albumName + "\n";
                                StringBuilder stringBuilder = new StringBuilder();
                                for(AlbumSongs albumSongs: albumSongsList){
                                    String stringToReturn = "\t Track: " + albumSongs.getTrack() +
                                            "\t Title: " + albumSongs.getTitle() + "\n";
                                    stringBuilder.append(stringToReturn);
                                }
                                System.out.println(start + stringBuilder.toString());
                                output.println(start + stringBuilder.toString() + "\0");
                            }
                            break;
                    }
                }
            }
            dataSources.close();
        } catch (IOException e){
            System.out.println("Server failure: " + e.getMessage());
        } finally {
            try{
                socket.close();
            } catch (IOException e){
                System.out.println("Error closing Socket:" + e.getMessage());
            }
        }
    }
}
