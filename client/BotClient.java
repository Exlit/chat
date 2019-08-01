package com.javarush.task.task30.task3008.client;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotClient extends Client {
    public class BotSocketThread extends SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            System.out.println(message);
            String[] mess;
            if(message.contains(": ")) {
                mess = message.split(": ");
                if (mess[1].equals("дата"))
                {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.YYYY");
                    Calendar cal = Calendar.getInstance();
                    sendTextMessage("Информация для " + mess[0] + ": " + dateFormat.format(cal.getTime()));
                }
                else if(mess[1].equals("день"))
                {
                    SimpleDateFormat dayFormat = new SimpleDateFormat("d");
                    Calendar cal = Calendar.getInstance();
                    sendTextMessage("Информация для " + mess[0] + ": " + dayFormat.format(cal.getTime()));
                }
                else if(mess[1].equals("месяц"))
                {
                    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
                    Calendar cal = Calendar.getInstance();
                    sendTextMessage("Информация для " + mess[0] + ": " + monthFormat.format(cal.getTime()));
                }
                else if(mess[1].equals("год"))
                {
                    SimpleDateFormat yearFormat = new SimpleDateFormat("YYYY");
                    Calendar cal = Calendar.getInstance();
                    sendTextMessage("Информация для " + mess[0] + ": " + yearFormat.format(cal.getTime()));
                }
                else if(mess[1].equals("время"))
                {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("H:mm:ss");
                    Calendar cal = Calendar.getInstance();
                    sendTextMessage("Информация для " + mess[0] + ": " + timeFormat.format(cal.getTime()));
                }
                else if(mess[1].equals("час"))
                {
                    SimpleDateFormat hourFormat = new SimpleDateFormat("H");
                    Calendar cal = Calendar.getInstance();
                    sendTextMessage("Информация для " + mess[0] + ": " + hourFormat.format(cal.getTime()));
                }
                else if(mess[1].equals("минуты"))
                {
                    SimpleDateFormat minuteFormat = new SimpleDateFormat("m");
                    Calendar cal = Calendar.getInstance();
                    sendTextMessage("Информация для " + mess[0] + ": " + minuteFormat.format(cal.getTime()));
                }
                else if(mess[1].equals("секунды"))
                {
                    SimpleDateFormat secFormat = new SimpleDateFormat("s");
                    Calendar cal = Calendar.getInstance();
                    sendTextMessage("Информация для " + mess[0] + ": " + secFormat.format(cal.getTime()));
                }
            }
        }
    }


    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    protected String getUserName() {
        int x = (int) (Math.random() * 100);
        return "date_bot_" + x;
    }

    public static void main(String[] args) {
        BotClient bot = new BotClient();
        bot.run();
    }
}
