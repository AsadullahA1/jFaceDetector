package javaFaceDetector;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.*;
import java.awt.FlowLayout;

public class Facedetector {

    // Path to Haar Cascade XML file (keep hardcoded)
	static final String CASCADE_PATH ="lib/haarcascade_frontalface_default.xml";

    // Target height for resizing
    static final int TARGET_HEIGHT = 400;

    public static void main(String[] args) {

        // Load OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // 1️⃣ Let user select an image
        String imagePath = selectImageFile();
        if (imagePath == null) return;

        // 2️⃣ Load image
        Mat image = loadImage(imagePath);
        if (image == null) return;

        // 3️⃣ Resize to fixed height 400 px
        image = resizeToHeight(image, TARGET_HEIGHT);

        // 4️⃣ Detect faces
        MatOfRect faces = detectFaces(image, CASCADE_PATH);
        if (faces == null) return;

        // 5️⃣ Draw rectangles around faces
        drawRectangles(image, faces);

        // 6️⃣ Display the result
        showImage(image, "Detected Faces");

        // Print number of faces detected
        System.out.println("Faces detected: " + faces.toArray().length);
    }

    // Function: Open file chooser to select image
    static String selectImageFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Image for Face Detection");

        // Filter image files
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image Files", "jpg", "jpeg", "png", "bmp"));

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            System.out.println("No file selected!");
            return null;
        }
    }

    // Function 1: Load image
    static Mat loadImage(String path) {
        Mat image = Imgcodecs.imread(path);
        if (image.empty()) {
            System.out.println("Image not loaded! Check path: " + path);
            return null;
        }
        return image;
    }

    // Function 2: Detect faces
    static MatOfRect detectFaces(Mat image, String cascadePath) {
        CascadeClassifier faceDetector = new CascadeClassifier(cascadePath);
        if (faceDetector.empty()) {
            System.out.println("Cascade file NOT loaded! Check path: " + cascadePath);
            return null;
        }
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);
        return faceDetections;
    }

    // Function 3: Draw rectangles on detected faces
    static void drawRectangles(Mat image, MatOfRect faces) {
        for (Rect rect : faces.toArray()) {
            Imgproc.rectangle(
                    image,
                    new org.opencv.core.Point(rect.x, rect.y),
                    new org.opencv.core.Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0), // green rectangle
                    2
            );
        }
    }

    // Resize image to fixed height (width auto-adjusts)
    static Mat resizeToHeight(Mat image, int targetHeight) {
        int originalWidth = image.width();
        int originalHeight = image.height();

        double scale = (double) targetHeight / originalHeight;
        int newWidth = (int) (originalWidth * scale);
        int newHeight = targetHeight;

        Mat resized = new Mat();
        Imgproc.resize(image, resized, new Size(newWidth, newHeight));
        return resized;
    }

    // Display Mat image in Swing window
    static void showImage(Mat img, String title) {
        Mat temp = new Mat();
        if (img.channels() == 1) {
            Imgproc.cvtColor(img, temp, Imgproc.COLOR_GRAY2BGR);
        } else {
            temp = img;
        }

        BufferedImage image = new BufferedImage(temp.width(), temp.height(), BufferedImage.TYPE_3BYTE_BGR);
        temp.get(0, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData());

        ImageIcon icon = new ImageIcon(image);
        JFrame frame = new JFrame(title);
        frame.setLayout(new FlowLayout());
        frame.setSize(temp.width() + 50, temp.height() + 50);
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
