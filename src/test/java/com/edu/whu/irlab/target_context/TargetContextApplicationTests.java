package com.edu.whu.irlab.target_context;

import com.edu.whu.irlab.target_context.service.Form;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TargetContextApplicationTests {

    @Test
    public void contextLoads() {
        String filePath="E:\\code\\target_context\\src\\main\\resources\\Case paper.xml";
        Form form=new Form();
        form.readFile(filePath);
        form.solve();
        form.write();//成功
    }

    @Test
    public void contextLoads1() {
        String filePath="E:\\code\\target_context\\src\\main\\resources\\Event Extraction in a Plot Advice Agent.xml";
        Form form=new Form();
        form.fileRead(filePath);
        form.refList();
        form.context();
        form.fileWrite();
    }

    @Test
    public void readFile(){
        String filePath="E:\\code\\target_context\\src\\main\\resources\\Case paper.xml";
        Form form=new Form();
        form.readFile(filePath);
    }

}

