package com.cue.splitter.view;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 10.08.12
 * Time: 2:37
 * To change this template use File | Settings | File Templates.
 */
public interface IFolderItemListener {

    void OnCannotFileRead(File file);//implement what to do folder is Unreadable
    void OnCannotFileWrite(File file);//implement what to do folder is Unreadable
    void OnFileClicked(File file);//What to do When a file is clicked
    void OnFolderChecked(File file);//What to do When a file is clicked

}
