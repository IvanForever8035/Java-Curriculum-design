package gdut;

import java.io.*;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.*;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.concurrent.*;

public class design1  extends Component {
	public DataInputStream data_in;
	private byte[] yuv_array;
	private int[] u_array, v_array;
	private int[] rgb_array;
	private BufferedImage img;
	private int width, height;
	private int frame_number;
    private int frame_size, yuv_frame_size;

    private int isAllowedWork=1;
    private boolean play_time=true;
    private Timer timer = null;
    private static TimerTask playTask = null;
    private boolean isUseOuter = true;
    private int playOrder=1;

    public design1(int width, int height, int d_frame_number, String filename, JFrame f) {
    	
    	this.width = width;
    	this.height = height;
    	frame_size = width * height;
    	frame_number = d_frame_number;
    	yuv_frame_size = (width * height * 3)>>1;
    	isUseOuter=true;
    	
    	//在Heap分配空间
    	img = new BufferedImage(width, height, 1);//1:TYPE_INT_RGB
    	yuv_array = new byte[yuv_frame_size];
		u_array = new int[frame_size];
    	v_array = new int[frame_size];
    	rgb_array = new int[frame_size];
    	
    	try {
    		FileInputStream f_in = new FileInputStream(new File(filename));
    		f_in.skip(frame_number * yuv_frame_size);
    		data_in = new DataInputStream(f_in);
    		data_in.read(yuv_array, 0, yuv_frame_size);
    		
    	} catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }
    	yuv2rgb();
    	img.setRGB(0, 0, width, height, rgb_array, 0, width);
    	
    		playTask = new TimerTask() {
        	int task_frame_number;
    	    public void run()
    		{
    	    	System.out.println("1");
    	    	if(isUseOuter) {
    	    		task_frame_number=frame_number;
    	    		isUseOuter = false;
    	    	}
    	    	if(isAllowedWork==0) {
//    	    		try {
//    					Thread.sleep(500);
//    				} catch (InterruptedException e) {
//    					e.printStackTrace();
//    				}
    	    	}else {
    		    	try {
    		    			data_in.read(yuv_array, 0, yuv_frame_size);
    		    			if(playOrder==0) {data_in.skip(-2 * yuv_frame_size);}
    		    			f.setTitle("YUV Player of GDUT           #" + task_frame_number + " frames");
    		    			if(playOrder==1) {
    			       		task_frame_number++;
    		    			}else if(playOrder==0) {
    			       		task_frame_number--;
    			       		if(task_frame_number<0) {
    			       			task_frame_number=0;
    			       			isAllowedWork=0;
    			       			JOptionPane.showMessageDialog(f, "已到最开始位置！");
    			       			}
    		    			}
    			        } catch (IOException e) {
    			        	e.printStackTrace();
    			            }
    		    		yuv2rgb();
    		    		img.setRGB(0, 0, width, height, rgb_array, 0, width);
    		    		repaint();      
    	    	}
    		}
    	};
    }
    
    
    private void yuv2rgb()
    {
    	int h;
    	int h2;
    	int frame_size2 = frame_size + (frame_size>>2);
    	int width2 = width<<1;
    	int i2, j2;
    	
    	h = 0;
    	h2 = 0;
    	for (int j = 0; j < (height>>1); j++)
    	{
    		for (int i = 0; i < (width>>1); i++)
    		{
    			i2 = i<<1;
    			int a, b;
    			u_array[h2 + i2]   = yuv_array[frame_size + h + i]&0xff;
    			v_array[h2 + i2]   = yuv_array[frame_size2 + h + i]&0xff;
    		}
    		h += width>>1;
    		h2 += width2;
    	}
    	//执行双线性内插，把4:1:1的YUV扩大为4:4:4的YUV
    	h2 = 0;
    	for (j2 = 0; j2 < height - 2; j2 += 2)
    	{
    		for (i2 = 0; i2 < width - 2; i2 += 2)
    		{
    			int a, b, ab;
    			
    			a = u_array[h2 + i2] + u_array[h2 + i2 + 2];//水平
    			b = u_array[h2 + i2] + u_array[h2 + i2 + width2];//垂直ֱ
    			ab = u_array[h2 + i2] + u_array[h2 + i2 + 2] + u_array[h2 + i2 + width2] + u_array[h2 + i2 + width2 + 2];//对角线
    			u_array[h2 + i2 + 1] = (a + 1)>>1;
    			u_array[h2 + i2 + width] = (b + 1)>>1;
    			u_array[h2 + i2 + width + 1] = (ab + 2)>>2;
    			
    			a = v_array[h2 + i2] + v_array[h2 + i2 + 2];//水平
    			b = v_array[h2 + i2] + v_array[h2 + i2 + width2];//垂直
    			ab = v_array[h2 + i2] + v_array[h2 + i2 + 2] + v_array[h2 + i2 + width2] + v_array[h2 + i2 + width2 + 2];//对角线
    			v_array[h2 + i2 + 1] = (a + 1)>>1;
    			v_array[h2 + i2 + width] = (b + 1)>>1;
    			v_array[h2 + i2 + width + 1] = (ab + 2)>>2;
    		}
			u_array[h2 + i2 + 1] = u_array[h2 + i2];
			u_array[h2 + i2 + width] = 
			u_array[h2 + i2 + width + 1] = (u_array[h2 + i2] + u_array[h2 + i2 + width2] + 1)>>1;
			
			v_array[h2 + i2 + 1] = v_array[h2 + i2];
			v_array[h2 + i2 + width] = 
			v_array[h2 + i2 + width + 1] = (v_array[h2 + i2] + v_array[h2 + i2 + width2] + 1)>>1;
			
			h2 += width2;
    	}
		for (i2 = 0; i2 < width - 2; i2 += 2)
		{
			int a, b, ab;
			
			u_array[h2 + i2 + 1] = 
			u_array[h2 + i2 + width + 1] = (u_array[h2 + i2] + u_array[h2 + i2 + 2] + 1)>>1;
			u_array[h2 + i2 + width] = u_array[h2 + i2];
			
			v_array[h2 + i2 + 1] = 
			v_array[h2 + i2 + width + 1] = (v_array[h2 + i2] + v_array[h2 + i2 + 2] + 1)>>1;
			v_array[h2 + i2 + width] = v_array[h2 + i2];
		}
		u_array[h2 + i2 + 1] =
		u_array[h2 + i2 + width] = 
		u_array[h2 + i2 + width + 1] = u_array[h2 + i2];   	
    	
		v_array[h2 + i2 + 1] =
		v_array[h2 + i2 + width] = 
		v_array[h2 + i2 + width + 1] = v_array[h2 + i2];
		
		//彩色空间变换,从YUV转换到RGB
		for (int i = 0; i < frame_size; i++)
		{
			int pixel_r, pixel_g, pixel_b;
			int pixel_y = yuv_array[i]&0xff;
			int pixel_u = u_array[i] - 128;
			int pixel_v = v_array[i] - 128;
			//YUV到RGB的矩阵变换运算
			double R = pixel_y - 0.001 * pixel_u + 1.402 * pixel_v;
			double G = pixel_y - 0.344 * pixel_u - 0.714 * pixel_v;
			double B = pixel_y + 1.772 * pixel_u + 0.001 * pixel_v;
			//限幅
			if (R > 255) pixel_r = 255;
			else if (R < 0) pixel_r = 0;
			else pixel_r = (int)R;
			if (G > 255) pixel_g = 255;
			else if (G < 0) pixel_g = 0;
			else pixel_g = (int)G;
			if (B > 255) pixel_b = 255;
			else if (B < 0) pixel_b = 0;
			else pixel_b = (int)B;
			rgb_array[i] = (pixel_r<<16) | (pixel_g<<8) | pixel_b;
		}
    }

    public Dimension getPreferredSize() {
        if (img == null) {
             return new Dimension(width, height);
        } else {
           return new Dimension(img.getWidth(null), img.getHeight(null));
       }
    }
    
    public void writeFile(String formatName, String filename) {
        try {
            ImageIO.write(img, formatName, new File(filename));
        } catch (IOException e) {
            e.printStackTrace();  
        }
    }
    
    public void paint(Graphics g) {
    	g.drawImage(img, 0, 0, null);
    }
    
    public void play(JFrame f,int rate) {
    	int m=(int)(1000*(1.0/rate));
    	timer = new Timer();
    	timer.schedule(playTask, 0, m);
    }
    
    public void timerRun(JFrame f,int rate)
    {
    	isAllowedWork=1;
    	if(play_time) {play(f,rate);play_time=false;}
    	play_time=false;
    }
    public void timerPause()
    {
    	isAllowedWork=0;
    }
    public void timerCancel()
    {
    	isAllowedWork=0;
    }
    
    public void timerChange(String filename,JFrame video,int changed_frame_number)
    {
    	String title=video.getTitle();
    	Pattern pattern = Pattern.compile("\\d+");
    	Matcher matcher = pattern.matcher(title);
    	if (matcher.find()) {
    	    frame_number = Integer.parseInt(matcher.group());
    	}
    	frame_number += changed_frame_number; // 更新当前帧号
        if(frame_number<0) {
       		frame_number=0;
       	}
        
        try {
            FileInputStream f_in = new FileInputStream(new File(filename));
            f_in.skip(frame_number * yuv_frame_size); // 将文件指针定位到对应帧的位置
            data_in = new DataInputStream(f_in);
            data_in.read(yuv_array, 0, yuv_frame_size); // 读取该帧的数据
            } catch (IOException e) {  
                e.printStackTrace();  
            }
        video.setTitle("YUV Player of GDUT           #" + frame_number + " frames");
        yuv2rgb(); // 转换为RGB格式
        img.setRGB(0, 0, width, height, rgb_array, 0, width); // 更新显示图像
        repaint();
        isUseOuter=true;
    }
    
    public void goBack0(String filename,JFrame video) 
    {
    	String title=video.getTitle();
    	Pattern pattern = Pattern.compile("\\d+");
    	Matcher matcher = pattern.matcher(title);
    	if (matcher.find()) {
    	    frame_number = Integer.parseInt(matcher.group());
    	}
    	frame_number = 0; // 回到最开始
        
        try {
            FileInputStream f_in = new FileInputStream(new File(filename));
            f_in.skip(frame_number * yuv_frame_size); // 将文件指针定位到对应帧的位置
            data_in = new DataInputStream(f_in);
            data_in.read(yuv_array, 0, yuv_frame_size); // 读取该帧的数据
            } catch (IOException e) {  
                e.printStackTrace();  
            }
        video.setTitle("YUV Player of GDUT           #" + frame_number + " frames");
        yuv2rgb(); // 转换为RGB格式
        img.setRGB(0, 0, width, height, rgb_array, 0, width); // 更新显示图像
        repaint();
        isUseOuter=true;
    }
    
    public void timerChoose(int play) 
    {
    	playOrder=play;
    }
}