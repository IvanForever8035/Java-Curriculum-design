package gdut;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.concurrent.*;

import gdut.design1;

import javax.swing.border.*;

class MainScreen extends JFrame implements ActionListener,ItemListener{
    private JPanel actionPanel = new JPanel();
    private JPanel framePanel = new JPanel();
    private JPanel playParamsPanel = new JPanel();
    
    private JButton openFileButton = new JButton("打开文件");
    private JButton closeFileButton = new JButton("关闭文件");
    private JButton playButton = new JButton("播放");
    private JButton pauseButton = new JButton("暂停");
    private JButton nextButton = new JButton("下一帧");
    private JButton next5Button = new JButton("下五帧");
    private JButton prevButton = new JButton("上一帧");
    private JButton prev5Button = new JButton("上五帧");
    private JButton backTo0Button = new JButton("返回开头");
    private JButton changeOrderButton = new JButton("顺序");
    private JRadioButton cifRadioButton = new JRadioButton("CIF");
    private JRadioButton qcifRadioButton = new JRadioButton("QCIF");
    private JRadioButton otherRadioButton = new JRadioButton("其他");
    private JTextField frameWidthField = new JTextField(5);
    private JTextField frameHeightField = new JTextField(5);
    private JLabel widthLabel = new JLabel("宽");
    private JLabel heightLabel = new JLabel("高");
    private JLabel frameRateLabel = new JLabel("帧率");
    private JComboBox frameRateBox = new JComboBox();
    private JLabel begiFrRateBox = new JLabel("起始帧");
    private JTextField begiFrRateField = new JTextField(10);
    private JLabel endFrRateBox = new JLabel("结束帧");
    private JTextField endFrRateField = new JTextField(10);
    
    JFrame f = null;
    private int frame_rate=0;
    private boolean isTheFirstTime = true;
    File selectedFile=null;
	design1 d1 = null;
    
    MainScreen() {
        super("仿YUVviewer播放器");
        setLayout(null);
        
     // 对actionPanel的设置
     // 设置控制界面
        actionPanel.setLayout(new GridLayout(5,2));
        actionPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        
     // 添加打开文件按钮
        actionPanel.add(openFileButton);
        openFileButton.setEnabled(true);
        openFileButton.addActionListener(this);
     // 添加关闭文件按钮
        actionPanel.add(closeFileButton);
        closeFileButton.setEnabled(false);
        closeFileButton.addActionListener(this);
     // 添加播放按钮
        actionPanel.add(playButton);
        playButton.setEnabled(false);
        playButton.addActionListener(this);
     // 添加暂停按钮
        actionPanel.add(pauseButton);
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(this);
     // 添加上一帧按钮
        actionPanel.add(prevButton);
        prevButton.setEnabled(false);
        prevButton.addActionListener(this);
     // 添加上五帧按钮
        actionPanel.add(prev5Button);
        prev5Button.setEnabled(false);
        prev5Button.addActionListener(this);
     // 添加下一帧按钮
        actionPanel.add(nextButton);
        nextButton.setEnabled(false);
        nextButton.addActionListener(this);
     // 添加下五帧按钮
        actionPanel.add(next5Button);
        next5Button.setEnabled(false);
        next5Button.addActionListener(this);
     // 添加返回开头按钮
        actionPanel.add(backTo0Button);
        backTo0Button.setEnabled(false);
        backTo0Button.addActionListener(this);
     // 添加改变顺序按钮
        actionPanel.add(changeOrderButton);
        changeOrderButton.setEnabled(false);
        changeOrderButton.addActionListener(this);
     // 添加操作面板
        add(actionPanel);
        actionPanel.setBounds(500, 30, 240, 250);
        
        
     // 对framePanel的设置
        framePanel.setLayout(null);
        framePanel.setBorder(BorderFactory.createTitledBorder("帧尺寸"));
     // 添加CIF        
        framePanel.add(cifRadioButton);
        cifRadioButton.setBounds(5, 30, 55, 15);
        cifRadioButton.addItemListener(this);
     // 添加QCIF
        framePanel.add(qcifRadioButton);
        qcifRadioButton.setBounds(5, 75, 55, 15);
        qcifRadioButton.addItemListener(this);
     // 添加其他
        framePanel.add(otherRadioButton);
        otherRadioButton.setBounds(5, 120, 55, 15);
        otherRadioButton.addItemListener(this);
     // 使单选框不会复选
        ButtonGroup FrameGroup = new ButtonGroup();
        FrameGroup.add(cifRadioButton);
        FrameGroup.add(qcifRadioButton);
        FrameGroup.add(otherRadioButton);
     // 文字“宽”、“高”的摆放   
        framePanel.add(widthLabel);
        widthLabel.setBounds(10, 165, 60, 30);
        widthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        framePanel.add(heightLabel);
        heightLabel.setBounds(80, 165, 60, 30);
        heightLabel.setHorizontalAlignment(SwingConstants.CENTER); 
     // 文字框的摆放(宽)
        framePanel.add(frameWidthField);
        frameWidthField.setBounds(10, 200, 60, 30);
        frameWidthField.setEnabled(false);
     // (高)   
        framePanel.add(frameHeightField);
        frameHeightField.setBounds(80,200,60,30);
        frameHeightField.setEnabled(false);
     // framePanel放置   
        add(framePanel);
        framePanel.setBounds(20, 30, 150, 250);

     // playParamsPanel的设置
        playParamsPanel.setLayout(null);
        playParamsPanel.setBorder(BorderFactory.createTitledBorder("播放参数"));
     // 添加frameRateLabel
        playParamsPanel.add(frameRateLabel);
        frameRateLabel.setBounds(15, 40, 30, 15);
        frameRateLabel.setHorizontalAlignment(SwingConstants.CENTER);
     // 添加frameRateBox
        playParamsPanel.add(frameRateBox);
        frameRateBox.addItem("5");
        frameRateBox.addItem("10");
        frameRateBox.addItem("15");
        frameRateBox.addItem("20");
        frameRateBox.addItem("25");
        frameRateBox.addItem("30");
        frameRateBox.addItem("999");
        frameRateBox.setSelectedItem("30");
        playParamsPanel.add(frameRateBox);
        frameRateBox.setBounds(80, 20, 100, 50);
     // 添加begiFrRateBox
        playParamsPanel.add(begiFrRateBox);
        begiFrRateBox.setBounds(11, 120, 40, 15);
        begiFrRateBox.setHorizontalAlignment(SwingConstants.CENTER);
     // 添加begiFrRateField
        playParamsPanel.add(begiFrRateField);
        begiFrRateField.setBounds(80,110,100,40);
        begiFrRateField.setText("0");
     // 添加endFrRateBox
        playParamsPanel.add(endFrRateBox);
        endFrRateBox.setBounds(11, 200, 40, 15);
        endFrRateBox.setHorizontalAlignment(SwingConstants.CENTER);
     // 添加endFrRateField
        playParamsPanel.add(endFrRateField);
        endFrRateField.setBounds(80,190,100,40);
        endFrRateField.setText("0");
        
        add(playParamsPanel);
        playParamsPanel.setBounds(250, 30, 200, 250);
        
        setSize(800, 400);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    public void actionPerformed(ActionEvent ae)
    {
    	if(ae.getSource()==openFileButton)
    	{
    		JFileChooser fileChooser = new JFileChooser();
    	    if (!(cifRadioButton.isSelected())&&!(qcifRadioButton.isSelected())&&!(otherRadioButton.isSelected()))
    	    {
    	    	JOptionPane.showMessageDialog(null, "未设置帧尺寸！");
    	    }else {
    	    	int result = fileChooser.showOpenDialog(this);
    	    	if (result == JFileChooser.APPROVE_OPTION) {
    	    		frame_rate=Integer.parseInt((String)frameRateBox.getSelectedItem());
        	        // 用户已经打开了一个文件
    	    		setButtonsEnabled(true);
        	        selectedFile = fileChooser.getSelectedFile(); 
        	        if(true) {
        	        	f = new JFrame("YUV Player of GDUT");
        	        }
        	        
        	        f.addWindowListener(new WindowAdapter() {
        	            public void windowClosing(WindowEvent e) {
        	            	System.exit(0);
        	            	}
        	        });
        	        int want_begin_frame=Integer.parseInt(begiFrRateField.getText());
        	        d1 = new design1(Integer.parseInt(frameWidthField.getText()),Integer.parseInt(frameHeightField.getText()),want_begin_frame,selectedFile.getAbsolutePath(),f);
        	        f.add("Center", d1);
        	        f.pack();
        	        f.setVisible(true);
        	        f.setAlwaysOnTop(true);    	 
        	        isTheFirstTime=false;
        	    } else if (result == JFileChooser.CANCEL_OPTION) {
        	        // 用户未打开文件
        	        JOptionPane.showMessageDialog(null, "文件未能打开！");;
        	    }
    	    }
    	}else if(ae.getSource()==playButton) {
    		d1.timerRun(f, frame_rate); 
        	playButton.setEnabled(false);
        	pauseButton.setEnabled(true);
    	}else if(ae.getSource()==pauseButton) {
    		pauseButton.setEnabled(false);
            playButton.setEnabled(true);
    		d1.timerPause();
    	}else if(ae.getSource()==closeFileButton) {
    		setButtonsEnabled(false);
    		d1.timerCancel();
    		f.dispose();
    	}else if(ae.getSource()==prevButton) {
    		d1.timerChange(selectedFile.getAbsolutePath(),f,-1);
    	}else if(ae.getSource()==prev5Button) {
    		d1.timerChange(selectedFile.getAbsolutePath(),f,-5);
    	}else if(ae.getSource()==nextButton) {
    		d1.timerChange(selectedFile.getAbsolutePath(),f,1);
    	}else if(ae.getSource()==next5Button) {
    		d1.timerChange(selectedFile.getAbsolutePath(),f,5);
    	}else if(ae.getSource()==backTo0Button) {
    		d1.goBack0(selectedFile.getAbsolutePath(),f);
    	}else if(ae.getSource()==changeOrderButton) {
    		if(changeOrderButton.getText()=="顺序") 
        	{
    			d1.timerChoose(0);
        		changeOrderButton.setText("倒序");
        	}else if(changeOrderButton.getText()=="倒序")
        	{
        		d1.timerChoose(1);
        		changeOrderButton.setText("顺序");
        	}
    	}
    }
    
    public void itemStateChanged(ItemEvent ie)
    {
    	if(ie.getStateChange()==ItemEvent.SELECTED)
    	{
    		if(ie.getSource()==cifRadioButton)
        	{
        		setFrameSize("352", "288");
                enableFrameFields(false);   		
        	}else if(ie.getSource()==qcifRadioButton)
        	{
        		setFrameSize("176", "144");
                enableFrameFields(false);
        	}else if(ie.getSource()==otherRadioButton)
        	{
        		frameWidthField.setText("");
                frameHeightField.setText("");
                enableFrameFields(true);
        	}
    	}
    }
    
    private void setButtonsEnabled(boolean enabled) {
        openFileButton.setEnabled(!enabled);
        closeFileButton.setEnabled(enabled);
        playButton.setEnabled(enabled);
        pauseButton.setEnabled(false);
        prevButton.setEnabled(enabled);
        prev5Button.setEnabled(enabled);
        nextButton.setEnabled(enabled);
        next5Button.setEnabled(enabled);
        backTo0Button.setEnabled(enabled);
        changeOrderButton.setEnabled(enabled);
    }

    private void setFrameSize(String width, String height) {
        frameWidthField.setText(width);
        frameHeightField.setText(height);
    }

    private void enableFrameFields(boolean enabled) {
        frameWidthField.setEnabled(enabled);
        frameHeightField.setEnabled(enabled);
    }
}

public class screen {
    public static void main(String[] args) {
        new MainScreen();
    }
}