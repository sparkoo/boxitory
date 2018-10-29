package cz.sparko.boxitory.test.e2e;

import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DownloadVersionTest extends AbstractIntegrationTest {
    private static final String VM = "f25";

    @Override
    public void createFolderStructure() throws IOException {
        super.createFolderStructure();

        File boxDir = createDirInRepository(VM);
        createFile(boxDir.getPath() + File.separator + VM + "_1_vbox.box");
        File vbox2 = createFile(boxDir.getPath() + File.separator + VM + "_2_vbox.box");
        try (FileWriter fw = new FileWriter(vbox2)) {
            fw.write("blabol vbox2");
        }
        createFile(boxDir.getPath() + File.separator + VM + "_3_vbox.box");
        File vbox4 = createFile(boxDir.getPath() + File.separator + VM + "_4_vbox.box");
        try (FileWriter fw = new FileWriter(vbox4)) {
            fw.write("blabol vbox latest 4");
        }

        File lbox1 = createFile(boxDir.getPath() + File.separator + VM + "_1_libvirt.box");
        try (FileWriter fw = new FileWriter(lbox1)) {
            fw.write("bla bla libvirt 1");
        }
        createFile(boxDir.getPath() + File.separator + VM + "_2_libvirt.box");
        File lbox3 = createFile(boxDir.getPath() + File.separator + VM + "_3_libvirt.box");
        try (FileWriter fw = new FileWriter(lbox3)) {
            fw.write("blabol libvirt latest 3");
        }
    }

    @Test
    public void givenValidSetup_whenDownloadExactVersionVbox_thenBoxDownloaded() throws Exception {
        mockMvc.perform(get("/download/" + VM + "/vbox/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(content().string("blabol vbox2"));
    }

    @Test
    public void givenValidSetup_whenDownloadLatestVbox_thenProperVersionDownloaded() throws Exception {
        mockMvc.perform(get("/download/" + VM + "/vbox/latest"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(content().string("blabol vbox latest 4"));
    }

    @Test
    public void givenValidSetup_whenDownloadExactLibvirt_thenProperVersionDownloaded() throws Exception {
        mockMvc.perform(get("/download/" + VM + "/libvirt/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(content().string("bla bla libvirt 1"));
    }

    @Test
    public void givenValidSetup_whenDownloadLatestLibvirt_thenProperVersionDownloaded() throws Exception {
        mockMvc.perform(get("/download/" + VM + "/libvirt/latest"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(content().string("blabol libvirt latest 3"));
    }

    @Test
    public void whenDownloadMissingVersion_then404() throws Exception {
        mockMvc.perform(get("/download/" + VM + "/libvirt/5"))
                .andDo(print())
                .andExpect(status().is(404));
    }

    @Test
    public void whenDownloadMissingProvider_then404() throws Exception {
        mockMvc.perform(get("/download/" + VM + "/blabol/1"))
                .andDo(print())
                .andExpect(status().is(404));
    }

    @Test
    public void whenDownloadMissingBox_then404() throws Exception {
        mockMvc.perform(get("/download/blabol/libvirt/1"))
                .andDo(print())
                .andExpect(status().is(404));
    }
}
