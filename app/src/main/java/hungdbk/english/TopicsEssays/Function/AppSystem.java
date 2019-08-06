package hungdbk.english.TopicsEssays.Function;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import hungdbk.english.TopicsEssays.Model.VocabKeys;
import hungdbk.english.TopicsEssays.R;
import hungdbk.english.TopicsEssays.Model.Essay;
import hungdbk.english.TopicsEssays.Model.Topic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

public class AppSystem {
    public static final String APP_DIRECTORY = "EnglishTopicsEssays2";
    public static final Boolean HAS_DB_HANDLER;
    public static int[] arrIcons;
    public static String[] baseDBFileName;

    static {
        HAS_DB_HANDLER = Boolean.valueOf(true);
        baseDBFileName = new String[]{"data/data_all_01.txt", "data/data_all_02.txt", "data/data_all_03.txt", "data/data_all_04.txt", "data/data_all_05.txt", "data/data_all_06.txt", "data/data_all_07.txt", "data/data_all_08.txt"};
        arrIcons = new int[]{R.drawable.ic_000, R.drawable.ic_001, R.drawable.ic_002, R.drawable.ic_003, R.drawable.ic_004, R.drawable.ic_005, R.drawable.ic_006, R.drawable.ic_007, R.drawable.ic_008, R.drawable.ic_009, R.drawable.ic_010, R.drawable.ic_011, R.drawable.ic_012, R.drawable.ic_013, R.drawable.ic_014, R.drawable.ic_015, R.drawable.ic_016, R.drawable.ic_017, R.drawable.ic_018, R.drawable.ic_019, R.drawable.ic_020, R.drawable.ic_021, R.drawable.ic_022, R.drawable.ic_023, R.drawable.ic_024, R.drawable.ic_025, R.drawable.ic_026, R.drawable.ic_027, R.drawable.ic_028, R.drawable.ic_029, R.drawable.ic_030, R.drawable.ic_031, R.drawable.ic_032, R.drawable.ic_033, R.drawable.ic_034, R.drawable.ic_035, R.drawable.ic_036, R.drawable.ic_037, R.drawable.ic_038, R.drawable.ic_039, R.drawable.ic_040, R.drawable.ic_041, R.drawable.ic_042, R.drawable.ic_043, R.drawable.ic_044, R.drawable.ic_045, R.drawable.ic_046, R.drawable.ic_047, R.drawable.ic_048, R.drawable.ic_049, R.drawable.ic_050, R.drawable.ic_051, R.drawable.ic_052, R.drawable.ic_053, R.drawable.ic_054, R.drawable.ic_055, R.drawable.ic_056, R.drawable.ic_057, R.drawable.ic_058, R.drawable.ic_059, R.drawable.ic_060, R.drawable.ic_061, R.drawable.ic_062, R.drawable.ic_063, R.drawable.ic_064, R.drawable.ic_065, R.drawable.ic_066, R.drawable.ic_067, R.drawable.ic_068, R.drawable.ic_069, R.drawable.ic_070, R.drawable.ic_071, R.drawable.ic_072, R.drawable.ic_073, R.drawable.ic_074, R.drawable.ic_075, R.drawable.ic_076, R.drawable.ic_077, R.drawable.ic_078, R.drawable.ic_079, R.drawable.ic_080, R.drawable.ic_081, R.drawable.ic_082, R.drawable.ic_083, R.drawable.ic_084, R.drawable.ic_085, R.drawable.ic_086, R.drawable.ic_087, R.drawable.ic_088, R.drawable.ic_089, R.drawable.ic_090, R.drawable.ic_091, R.drawable.ic_092, R.drawable.ic_093, R.drawable.ic_094, R.drawable.ic_095, R.drawable.ic_096, R.drawable.ic_097, R.drawable.ic_098, R.drawable.ic_099, R.drawable.ic_100, R.drawable.ic_101, R.drawable.ic_102, R.drawable.ic_103, R.drawable.ic_104, R.drawable.ic_105, R.drawable.ic_106, R.drawable.ic_107, R.drawable.ic_108, R.drawable.ic_109, R.drawable.ic_110, R.drawable.ic_111, R.drawable.ic_112, R.drawable.ic_113, R.drawable.ic_114, R.drawable.ic_115, R.drawable.ic_116, R.drawable.ic_117, R.drawable.ic_118, R.drawable.ic_119, R.drawable.ic_120, R.drawable.ic_121, R.drawable.ic_122, R.drawable.ic_123, R.drawable.ic_124, R.drawable.ic_125, R.drawable.ic_126, R.drawable.ic_127, R.drawable.ic_128, R.drawable.ic_129, R.drawable.ic_130, R.drawable.ic_131, R.drawable.ic_132, R.drawable.ic_133, R.drawable.ic_134, R.drawable.ic_135, R.drawable.ic_136, R.drawable.ic_137, R.drawable.ic_138, R.drawable.ic_139, R.drawable.ic_140, R.drawable.ic_141, R.drawable.ic_142, R.drawable.ic_143, R.drawable.ic_144, R.drawable.ic_145, R.drawable.ic_146, R.drawable.ic_147, R.drawable.ic_148, R.drawable.ic_149, R.drawable.ic_150, R.drawable.ic_151, R.drawable.ic_152, R.drawable.ic_153, R.drawable.ic_154, R.drawable.ic_155, R.drawable.ic_156, R.drawable.ic_157, R.drawable.ic_158, R.drawable.ic_159, R.drawable.ic_160, R.drawable.ic_161, R.drawable.ic_162, R.drawable.ic_163, R.drawable.ic_164, R.drawable.ic_165, R.drawable.ic_166, R.drawable.ic_167, R.drawable.ic_168, R.drawable.ic_169, R.drawable.ic_170, R.drawable.ic_171, R.drawable.ic_172, R.drawable.ic_173, R.drawable.ic_174, R.drawable.ic_175, R.drawable.ic_176, R.drawable.ic_177, R.drawable.ic_178, R.drawable.ic_179, R.drawable.ic_180, R.drawable.ic_181, R.drawable.ic_182, R.drawable.ic_183, R.drawable.ic_184, R.drawable.ic_185};
    }

    public static int binarySearch(int value, int[] arr) {
        int left = 0;
        int right = arr.length;
        while (left < right) {
            int middle = (left + right) / 2;
            if (arr[middle] == value) {
                return middle;
            }
            if (arr[middle] > value) {
                right = middle;
            } else {
                left = middle + 1;
            }
        }
        return -1;
    }

    public static int binarySearch(int value, ArrayList<Integer> arr) {
        int left = 0;
        int right = arr.size();
        while (left < right) {
            int middle = (left + right) / 2;
            if (arr.get(middle) == value) {
                return middle;
            }
            if (arr.get(middle) > value) {
                right = middle;
            } else {
                left = middle + 1;
            }
        }
        return -1;
    }

    public static boolean isExistedOrder(Cursor cursor, int order) {
        boolean isExisted = false;
        if (cursor != null && cursor.getCount() > 0) {
            int[] arrOrder = new int[cursor.getCount()];
            int i = 0;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int i2 = i + 1;
                arrOrder[i] = cursor.getInt(0);
                cursor.moveToNext();
                i = i2;
            }
            isExisted = binarySearch(order, arrOrder) != -1;
        }
        cursor.close();
        return isExisted;
    }

    public static int getNextOrder(Cursor cursor) {
        int next = 1;
        if (cursor != null && cursor.getCount() > 0) {
            int[] arrOrder = new int[cursor.getCount()];
            int i = 0;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int i2 = i + 1;
                arrOrder[i] = cursor.getInt(0);
                cursor.moveToNext();
                i = i2;
            }
            next = arrOrder[arrOrder.length - 1] + 1;
            if (arrOrder[0] > 1) {
                next = 1;
            } else {
                for (int j = 0; j < arrOrder.length - 1; j++) {
                    if (arrOrder[j + 1] > arrOrder[j] + 1) {
                        next = arrOrder[j] + 1;
                        break;
                    }
                }
            }
        }
        cursor.close();
        return next;
    }

    public static int getForwardId(Cursor cursor, int order) {
        if (cursor == null || cursor.getCount() <= 0) {
            return 0;
        }
        int[] arrOrder = new int[cursor.getCount()];
        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int i2 = i + 1;
            arrOrder[i] = cursor.getInt(1);
            cursor.moveToNext();
            i = i2;
        }
        int curIndex = binarySearch(order, arrOrder);
        if (curIndex == -1) {
            return 0;
        }
        if (curIndex < cursor.getCount() - 1) {
            cursor.moveToPosition(curIndex + 1);
        } else {
            cursor.moveToFirst();
        }
        return cursor.getInt(0);
    }

    public static int getBackwardId(Cursor cursor, int order) {
        if (cursor == null || cursor.getCount() <= 0) {
            return 0;
        }
        int[] arrOrder = new int[cursor.getCount()];
        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int i2 = i + 1;
            arrOrder[i] = cursor.getInt(1);
            cursor.moveToNext();
            i = i2;
        }
        int curIndex = binarySearch(order, arrOrder);
        if (curIndex == -1) {
            return 0;
        }
        if (curIndex > 0) {
            cursor.moveToPosition(curIndex - 1);
        } else {
            cursor.moveToLast();
        }
        return cursor.getInt(0);
    }

    public static ArrayList<Integer> getRandomArray(Cursor cursor, int number) {

        ArrayList<Integer> result = new java.util.ArrayList<Integer>();
        ArrayList<Integer> arrId = new java.util.ArrayList<Integer>();

        if (cursor == null || cursor.getCount() <= 0) return result;

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            arrId.add(Integer.valueOf(cursor.getInt(0)));
            cursor.moveToNext();
        }
        Log.d("XXX", "SIZE = "+arrId.size());
        if (arrId.size() <= number) {
            return arrId;
        } else {
            while (true) {
                Random random = new Random();
                int x = arrId.get(random.nextInt(arrId.size()));
                if (binarySearch(x, result) == -1) {
                    result.add(Integer.valueOf(x));
                    if (result.size() == number) break;
                }
            }
        }

        return result;
    }

    public static StringBuilder readFileFromResource(Context context, int resourceId) throws NotFoundException, IOException {
        StringBuilder builder = new StringBuilder();
        try {
            InputStream in = context.getResources().openRawResource(resourceId);
            BufferedReader buffReader = new BufferedReader(new InputStreamReader(in));
            String str = "";
            while (true) {
                str = buffReader.readLine();
                if (str != null) {
                    builder.append(str);
                    builder.append("\n");
                } else {
                    in.close();
                    try {
                        builder.deleteCharAt(builder.lastIndexOf("\n"));
                        return builder;
                    } catch (Exception e) {
                        return builder;
                    }
                }
            }
        } catch (IOException e2) {
            return null;
        } catch (NotFoundException e3) {
            return null;
        }
    }

    public static StringBuilder readFileFromAssets(Context context, String fileName) {
        StringBuilder builder = new StringBuilder();
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            BufferedReader buffReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String str = "";
            while (true) {
                str = buffReader.readLine();
                if (str != null) {
                    builder.append(str);
                    builder.append("\n");
                } else {
                    in.close();
                    try {
                        builder.deleteCharAt(builder.lastIndexOf("\n"));
                        return builder;
                    } catch (Exception e) {
                        return builder;
                    }
                }
            }
        } catch (IOException e2) {
            return null;
        }
    }

    public static StringBuilder readFileFromSDCard(Context context, String directory, String fileName) {
        StringBuilder builder = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + directory + "/" + fileName));
            String line = "";
            while (scanner.hasNext()) {
                builder.append(scanner.nextLine());
                builder.append("\n");
            }
            scanner.close();
            try {
                builder.deleteCharAt(builder.lastIndexOf("\n"));
                return builder;
            } catch (Exception e) {
                return builder;
            }
        } catch (IllegalStateException e2) {
            return null;
        } catch (NoSuchElementException e3) {
            return null;
        } catch (FileNotFoundException e4) {
            Toast.makeText(context, "Error: Failure to read file!\nFile data can't be found...", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public static void writeFileToSDCard(Context context, String directory, String fileName, String data, boolean appen) {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + directory);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dir + "/" + fileName);
        try {
            file.createNewFile();
            if (file.canWrite()) {
                BufferedWriter fo = new BufferedWriter(new FileWriter(file, appen));
                fo.write(data);
                fo.close();
            }
        } catch (IOException e) {
        }
    }

    public static ArrayList<StringBuilder> parseData(StringBuilder data, String filter) {
        if (data == null || filter == null) {
            return null;
        }
        ArrayList<StringBuilder> arrData = new ArrayList();
        while (data.lastIndexOf(filter) >= 0) {
            try {
                String element = data.substring(data.lastIndexOf(filter));
                data.delete(data.lastIndexOf(filter), data.length() - 1);
                arrData.add(0, new StringBuilder(element));
            } catch (NullPointerException e) {
                return null;
            } catch (StringIndexOutOfBoundsException e2) {
                return null;
            } catch (IndexOutOfBoundsException e3) {
                return null;
            }
        }
        return arrData;
    }

    public static ArrayList<Topic> buildDatabase(StringBuilder data) {
        try {
            ArrayList<Topic> arrayList = new ArrayList();
            ArrayList<StringBuilder> arrTopicData = parseData(data, "Topic");
            for (int i = 0; i < arrTopicData.size(); i++) {
                if (((StringBuilder) arrTopicData.get(i)).indexOf("Essay") != -1) {
                    int j;
                    String topic = ((StringBuilder) arrTopicData.get(i)).substring(0, ((StringBuilder) arrTopicData.get(i)).indexOf("Essay") - 1);
                    int tpOrder = Integer.parseInt(topic.substring(topic.indexOf("Topic ") + 6, topic.indexOf(9) - 1));
                    String title = "";
                    StringBuilder stringBuilder = new StringBuilder("");
                    String detail = "";
                    if (((StringBuilder) arrTopicData.get(i)).indexOf("@") != -1) {
                        stringBuilder = new StringBuilder(topic.substring(topic.indexOf(9) + 1, topic.indexOf("@") - 1));
                        for (j = 0; j < stringBuilder.length() - 1; j++) {
                            if (stringBuilder.charAt(j) == ' ') {
                                stringBuilder.setCharAt(j + 1, Character.toUpperCase(stringBuilder.charAt(j + 1)));
                            }
                        }
                        detail = topic.substring(topic.indexOf("@") + 1);
                    } else {
                        title = "";
                        detail = topic.substring(topic.indexOf(9) + 1);
                    }
                    ArrayList<Essay> arrEssays = new ArrayList();
                    ArrayList<StringBuilder> arrEssayData = parseData(new StringBuilder((CharSequence) arrTopicData.get(i)), "Essay");
                    for (j = 0; j < arrEssayData.size(); j++) {
                        StringBuilder content;
                        StringBuilder content2;
                        String essay = ((StringBuilder) arrEssayData.get(j)).toString();
                        int esOrder = Integer.parseInt(essay.substring(essay.indexOf("Essay ") + 6, essay.indexOf(": ")));
                        String notes = "";
                        if (essay.indexOf("@Notes:") != -1) {
                            content = new StringBuilder(essay.substring(essay.indexOf(": ") + 2, essay.indexOf("@Notes:")));
                            if (essay.substring(essay.indexOf("@Notes: ")).trim().length() >= 9) {
                                notes = essay.substring(essay.indexOf("@Notes:") + 8, essay.lastIndexOf("\n") + 1);
                            } else {
                                notes = "";
                            }
                            content2 = content;
                        } else {
                            content = new StringBuilder(essay.substring(essay.indexOf(": ") + 2, essay.lastIndexOf("\n")));
                            notes = "";
                            content2 = content;
                        }
                        content = new StringBuilder(content2.toString().trim());
                        content.insert(0, "<p>");
                        for (int k = 5; k < content.length() - 1; k++) {
                            if (content.charAt(k) == '\n') {
                                content.replace(k, k + 1, "</p><p>");
                            }
                        }
                        content.append("</p>");
                        arrEssays.add(new Essay(esOrder, content.toString(), "", notes));
                    }
                    arrayList.add(new Topic(tpOrder, title.toString(), detail, arrEssays));
                }
            }
            return arrayList;
        } catch (Exception e) {
            return null;
        }
    }

    public static void createDatabase(Context context, DatabaseHelper dbHelper, ArrayList<Topic> arrTopic) {
        TopicHelper tpHelper = new TopicHelper(context);
        EssayHelper esHelper = new EssayHelper(context);
        int i = 0;
        while (i < arrTopic.size()) {
            try {
                int tpOrder = ((Topic) arrTopic.get(i)).getOrder();
                tpHelper.insertTopic(tpOrder, new Topic(tpOrder, ((Topic) arrTopic.get(i)).getTitle(), ((Topic) arrTopic.get(i)).getDetail()), VocabKeys.UNCHECKED);
                int lastTopicId = tpHelper.getMaxTopicId();
                for (int j = 0; j < ((Topic) arrTopic.get(i)).getArrEssay().size(); j++) {
                    esHelper.insertEssay(esHelper.getMaxEssayId() + 1, new Essay(((Essay) ((Topic) arrTopic.get(i)).getArrEssay().get(j)).getOrder(), ((Essay) ((Topic) arrTopic.get(i)).getArrEssay().get(j)).getContent(), "brand new!", ((Essay) ((Topic) arrTopic.get(i)).getArrEssay().get(j)).getNote()), VocabKeys.UNCHECKED, lastTopicId);
                }
                i++;
            } catch (Exception e) {
                return;
            }
        }
    }

    public static void createDatabase(Context context, DatabaseHelper dbHelper, Topic topic, int topicId) {
        TopicHelper tpHelper = new TopicHelper(context);
        EssayHelper esHelper = new EssayHelper(context);
        tpHelper.updateTopic(topicId, topicId, topic, VocabKeys.UNCHECKED);
        esHelper.deleteEssaysOfTopic(topicId);
        for (int j = 0; j < topic.getArrEssay().size(); j++) {
            int esOrder = ((Essay) topic.getArrEssay().get(j)).getOrder();
            esHelper.insertEssay(esOrder, new Essay(esOrder, ((Essay) topic.getArrEssay().get(j)).getContent(), "brand new!", ((Essay) topic.getArrEssay().get(j)).getNote()), VocabKeys.UNCHECKED, topicId);
        }
    }

    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        Bitmap targetBitmap = Bitmap.createBitmap(50, 50, Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle((((float) 50) - 1.0f) / 2.0f, (((float) 50) - 1.0f) / 2.0f, Math.min((float) 50, (float) 50) / 2.0f, Direction.CCW);
        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight()), new Rect(0, 0, 50, 50), null);
        return targetBitmap;
    }
}
