package cz.sparko.boxitory.test.integration.versionsort;

import cz.sparko.boxitory.test.integration.AbstractIntegrationTest;

import java.io.File;
import java.io.IOException;

abstract public class VersionSortTest extends AbstractIntegrationTest {
    final String VM = "vm";
    final String VM_1_VBOX = VM + "_1_virtualbox.box";
    final String VM_2_VBOX = VM + "_2_virtualbox.box";
    final String VM_3_VBOX = VM + "_3_virtualbox.box";
    final String VM_5_VBOX = VM + "_5_virtualbox.box";
    final String VM_12_VBOX = VM + "_12_virtualbox.box";

    @Override
    public void createFolderStructure() throws IOException {
        createRepositoryDir();
        File vmDir = createDirInRepository(VM);
        createFile(vmDir.getPath() + File.separator + VM_5_VBOX);
        createFile(vmDir.getPath() + File.separator + VM_12_VBOX);
        createFile(vmDir.getPath() + File.separator + VM_3_VBOX);
        createFile(vmDir.getPath() + File.separator + VM_1_VBOX);
        createFile(vmDir.getPath() + File.separator + VM_2_VBOX);
    }
}
