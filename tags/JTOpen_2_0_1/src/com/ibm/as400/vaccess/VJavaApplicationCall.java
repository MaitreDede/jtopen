///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (AS/400 Toolbox for Java - OSS version)                              
//                                                                             
// Filename: VJavaApplicationCall.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.vaccess;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Enumeration;

import com.ibm.as400.access.ExtendedIllegalStateException;
import com.ibm.as400.access.ActionCompletedListener;
import com.ibm.as400.access.ActionCompletedEvent;
import com.ibm.as400.access.AS400;
import com.ibm.as400.access.JavaApplicationCall;
import com.ibm.as400.access.Trace;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

/**
 *
 *<P>
 *The VJavaApplicationCall class provides a visual interface to
 *com.ibm.as400.access.JavaApplicationCall.  The visual interface
 *consists of two components: an input field where the program
 *to run is specified and input is sent to the program, and
 *an output text area where the output from the program is displayed.
 *
 *<P>
 *GUI output generated by the Java program running on the AS/400
 *is not handled by this class.
 *As in JavaApplicationCall, the Java program running on the AS/400
 *receives input via standard.  The output text area displays output
 *the AS/400 java program writes to standard out and standard error.
 *GUI input/output must be handled via another mechanism such as
 *Remote AWT.
 *
 *<P>
 *There are two ways to set up the AS/400 Java environment:
 *<OL>
 *<LI>VJavaApplicationCall
 *must have a JavaApplicationCall object.  After creating the JavaApplicationCall
 *object, the application can set environment parameters on the JavaApplicationCall,
 *object, then construct
 *the VJavaApplicationCall object passing it the JavaApplicationCall object.
 *The advantage to this method is the application sets up the environment instead
 *of the user.
 *<LI>
 *The environment can be set up via
 *<B>set</B> commands entered in the GUI.
 *For additional information on these commands,
 *see the on-line help for the Java command. Valid commands are:
 *<UL>
 *<li>Classpath - the value of the CLASSPATH environment variable. Directories are separated by colons.
 *<li>DefaultPort - the default port for communicating standard in, standard out and standard error between the client and AS/400 java environment.
 *<li>FindPort - indicates if the client should search for a free port if the default port is in use.
 *<li>Interpret - indicates if all Java class files should be run interpretively.
 *<li>Optimize - the optimization level for classes not yet optimized.
 *<li>Options - additional options used when running the Java class.
 *<li>SecurityCheckLevel - the level of warnings given for directories in the CLASSPATH that have public write authority.
 *<li>GarbageCollectionFrequency - the relative frequency that garbage collection runs.
 *<li>GarbageCollectionInitialSize - the initial size, in kilobytes, of the garbage collection heap.
 *<li>GarbageCollectionMaximumSize - the maximum size, in kilobytes, that the garbage collection heap can grow to.
 *<li>GarbageCollectionPriority - the priority of the tasks running garbage collection.
 *</UL>
 *For example, to send the optimization level to 30, enter <BR>
 *<UL>
 *set optimize=30
 *</UL>
 *</OL>
 *
 *<P>
 *You start the Java application using the <B>java</B> command.  The
 *syntax for this command is much like the syntax when running the command
 *on the client.  It is
 *<UL>
 *java [-classpath=value] [-verbose] [-Dproperty=value -Dproperty=value [...]] class [parm1 parm2 [...]]]
 *</UL>
 *Note, this class correctly sets the standard in, standard out and standard
 *error properties so os400.stdin, os400.stdout
 *or os400stderr properties are ignored.
 *For example, to run Java application
 *<UL>
 *java -classpath=/myClasses:/myClasses/lib/package.jar myProgram parm1 parm2
 *</UL>
 *
 *To use this class you simply create a frame to contain the class
 *then call the load() method to run the start the application.  For example,
 *
 *<a name="ex"> </a>
 *<PRE>
 *
 *AS400 system = new AS400("myAS400");
 *
 * JavaApplicationCall javaCall  = new  JavaApplicationCall(system);
 *VJavaApplicationCall vJavaCall = new VJavaApplicationCall(javaCall);
 *
 *JFrame f = new JFrame ("JavaApplicationCallExample");
 *f.getContentPane().setLayout(new BorderLayout ());
 *f.getContentPane().add("Center", vJavaCall);
 *f.pack();
 *f.show();

**/
public class  VJavaApplicationCall extends JComponent
                                   implements KeyListener
{
  private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";

    // These variables represent event support.
    private transient ErrorEventSupport     errorEventSupport_;
    private transient PropertyChangeSupport propertyChangeSupport_;
    private transient VetoableChangeSupport vetoableChangeSupport_;
    private transient WorkingEventSupport   workingEventSupport_;

    // Private variable representing the object of JavaApplicationCall.
    private JavaApplicationCall javaAppCall_ = null;

    // Private variable representing the initial value of the classpath.
    private String classPath_ = "";

    // List of user commands
    private Vector inputVector_ ;

    // Index of current command in input vector
    private int inputIndex_ = 0;

    // Private variable representing string input by user.
    private String  inputStr_;

    // Private variable representing the object of JTextField into which user input command.
    private JTextField  inputText_;
    private JScrollPane scrollPane_;

    // Private variable, if true the program is complete
    private boolean javaRunOver_ = true;

    // Private variable representing the default value of Options.
    private String [] optionArray_ = {"*NONE"};

    // Private variable representing the object of JTextArea on which the output and error
    // messages are displayed.
    private JTextArea   outputText_;

    // Private variable representing the number of parameter in command string.
    private int paramNum_;

    // Private variable representing the object of StringTokenizer.
    // messages are displayed.
    private StringTokenizer strToken_;

    // Private variable representing the object of VJavaGetResult which is responsible
    // for running the remote java application and getting the result.
    private VJavaGetResult vJavaGetResult_;

    private boolean hasNext     = false;
    private boolean hasPrevious = false;
    private boolean firstDraw   = true;

    private long linesWritten_ = 0;
    private static final int ROWS = 24;
    private static final int COLUMNS = 72;

    /**
     * Constructs a VJavaApplicationCall object.
    **/
    public VJavaApplicationCall()
    {
        // javaAppCall_ = new JavaApplicationCall();
        initialize();
    }



    /**
     * Constructs a VJavaApplicationCall object. A JavaApplicationCall
     *              object defines the Java program to run.
     *
     * @param javaApplicationCall The access class which defines the
     *                            Java application to run.
    **/
    public VJavaApplicationCall(JavaApplicationCall javaApplicationCall)
    {
        if(javaApplicationCall == null)
            throw new NullPointerException ("JavaApplicationCall");

        javaAppCall_ = javaApplicationCall;
        initialize();
    }

    /**
     * Adds a listener to be notified when an error occurs.
     *
     * @see #removeErrorListener
     * @param listener The listener.
    **/
    public void addErrorListener( ErrorListener listener )
    {
        if (listener == null)
        {
           throw new NullPointerException("listener");
        }
        errorEventSupport_.addErrorListener(listener);
    }


    /**
     * Notification to VJavaApplicationCall that it now has a parent component.
     * The windowing system calls this method
     * when VJavaApplicationCall gets a parent frame.  When called
     * VJavaApplicationCall
     * requsts input focus be given to the input field.
     * <P>
     * <B>Since this method is
     * called by Swing at the appropriate time, application code should not
     * call this method.</B>
    **/
    public void addNotify()
    {
       super.addNotify();
       inputText_.requestFocus();
    }


    /**
     * Adds a listener to be notified when the value of any bound property
     * changes.
     *
     * @see #removePropertyChangeListener
     * @param  listener  The listener.
    **/
    public void addPropertyChangeListener( PropertyChangeListener listener )
    {
        if (listener == null)
        {
            throw new NullPointerException("listener");
        }

        propertyChangeSupport_.addPropertyChangeListener(listener);
    }

    /**
     * Adds a listener to be notified when the value of any constrained
     * property changes.
     *
     * @see #removeVetoableChangeListener
     * @param  listener  The listener.
    **/
    public void addVetoableChangeListener( VetoableChangeListener listener )
    {
        if (listener == null)
        {
            throw new NullPointerException("listener");
        }

        vetoableChangeSupport_.addVetoableChangeListener(listener);
    }

    /**
     * Adds a listener to be notified when work starts and stops on potentially long running operations.
     *
     * @param listener The listener.
    **/
    public void addWorkingListener(WorkingListener listener)
    {
        if (listener == null)
        {
            throw new NullPointerException("listener");
        }

        workingEventSupport_.addWorkingListener(listener);
    }



    // write output to the text window then move the scrollbar so the
    // user always sees the latest text.
    void appendOutput(String output)
    {
       outputText_.append(output);

       linesWritten_ ++;

       int tx = outputText_.getSize().width;
       int ty = outputText_.getSize().height;

       Rectangle rect = new Rectangle(0,ty-2,tx-1,ty-1);

       if (linesWritten_ < ROWS)
       {
          int pixelsPerLine = ty / ROWS;

          if (pixelsPerLine > 0)
          {
             int visableLines  = scrollPane_.getSize().height / pixelsPerLine;
             if (linesWritten_ > visableLines)
                outputText_.scrollRectToVisible(rect);
          }
       }
       else
       {
          if(ty > scrollPane_.getSize().height)
             outputText_.scrollRectToVisible(rect);
       }
    }





    /**
     * Stops all threads.
     @exception Throwable  If an error occurs during cleanup.
     **/
    protected void finalize() throws Throwable
    {
        vJavaGetResult_ = null;
        super.finalize();
    }

    /**
     *  Returns the access class which defines the Java application to run.
     *
     *  @return The the com.ibm.as400.access.JavaApplicationCall object
     *          which defines the Java application to run.
    **/
    public JavaApplicationCall getJavaApplicationCall()
    {
        return javaAppCall_;
    }

    /**
     *  Returns a reference to the JTextArea object. Standard output and
     *  standard error information from the AS/400 Java program are displayed
     *  in this text area.  The
     *  application can use the reference to the JTextArea to modify
     *  attributes such as the size of the text area
     *  or the font used to display text in the text area.
     *
     *  @return The JTextArea object which displays standard output
     *          and standard error from the AS/400 Java program.
    **/
    public JTextArea getOutputText()
    {
        return outputText_;
    }


    /**
     *    Initializes the event support and displays the GUI.
     **/
    private void initialize()
    {
        // Initialize the event support.
        errorEventSupport_     = new ErrorEventSupport(this);
        propertyChangeSupport_ = new PropertyChangeSupport(this);
        vetoableChangeSupport_ = new VetoableChangeSupport(this);
        workingEventSupport_   = new WorkingEventSupport (this);
        inputVector_ = new Vector();

        setLayout(new BorderLayout());
        JPanel part1=new JPanel(new BorderLayout());

        JLabel outputLabel = new JLabel(ResourceLoader.getText("REMOTE_OUTPUT_LABEL"));
        JLabel inputLabel = new JLabel(ResourceLoader.getText("REMOTE_INPUT_LABEL"));
        outputText_ = new JTextArea();
        outputText_.setEditable(false);
        outputText_.setRows(ROWS);
        outputText_.setColumns(COLUMNS);
        scrollPane_ = new JScrollPane(outputText_);
        scrollPane_.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputText_  = new JTextField();

        part1.add(outputLabel,BorderLayout.NORTH);
        part1.add(scrollPane_,BorderLayout.CENTER);

        JPanel part2=new JPanel(new BorderLayout());
        part2.add(inputLabel,BorderLayout.NORTH);
        part2.add(inputText_,BorderLayout.CENTER);

        add(part1,BorderLayout.CENTER);
        add(part2,BorderLayout.SOUTH);

        inputText_.addKeyListener(this);
        appendOutput(ResourceLoader.getText("REMOTE_PROMPT"));
    }


    /**
     * A key is pressed.
     * @param keyEvent  The key event.
     **/
    public void keyPressed(KeyEvent keyEvent){}


    /**
     * The key is released.
     * @param keyEvent  The key event.
     **/
    public void keyReleased(KeyEvent keyEvent)
    {
        if (javaAppCall_ == null)
           throw new ExtendedIllegalStateException("JavaApplicationCall",
            ExtendedIllegalStateException.PROPERTY_NOT_SET);

        switch (keyEvent.getKeyCode())
        {
            case KeyEvent.VK_ENTER :
                     processEnter();
                     break;
            case KeyEvent.VK_PAGE_DOWN :
                     if(inputVector_.size() >0)
                     {
                         inputIndex_ = inputVector_.size() -1;
                         inputText_.setText((String) inputVector_.elementAt(inputIndex_));
                     }
                     break;
            case KeyEvent.VK_PAGE_UP :
                     if(inputVector_.size()>0)
                     {
                         inputIndex_ = 0;
                         inputText_.setText((String) inputVector_.elementAt(inputIndex_));
                     }
                     break;
            case KeyEvent.VK_UP     :
                     if(inputVector_.size()>0)
                     {
                         if(hasPrevious) // press Up and get the previous command.
                         {
                             if(inputIndex_ >0)
                              inputIndex_ -= 1;
                             inputText_.setText((String) inputVector_.elementAt(inputIndex_));
                         }
                         else
                             inputText_.setText((String) inputVector_.elementAt(inputIndex_));
                         hasPrevious = true;
                         hasNext = true;
                     }
                     break;
            case KeyEvent.VK_DOWN :
                     if(inputVector_.size()>0)
                     {
                         if(hasNext)  // press Down and get the next command.
                         {
                             if(inputIndex_ < inputVector_.size()-1)
                                 inputIndex_ +=1;
                             inputText_.setText((String) inputVector_.elementAt(inputIndex_));
                             hasPrevious = true;
                         }
                     }
                     break;
            case KeyEvent.VK_ESCAPE :
                     inputText_.setText("");
        }
    }

    /**
     * The key is typed.
     * @param keyEvent  The key event.
     **/
    public void keyTyped(KeyEvent keyEvent){}




    /**
     * Loads information produced by the Java Application run on the AS/400.
    **/
    private void load()
    {
        if (vJavaGetResult_ == null)
            vJavaGetResult_ = new VJavaGetResult(this);

        try
        {
           javaAppCall_.getSystem().connectService(AS400.COMMAND);
        }
        catch (Exception e)
        {
           appendOutput(e.toString() + "\n");
           appendOutput("\n"+ResourceLoader.getText("REMOTE_PROMPT"));
           return;
        }

        appendOutput(ResourceLoader.getText("REMOTE_JAVA_START"));
        appendOutput(javaAppCall_.getJavaApplication()+"\n\n");
        vJavaGetResult_.play();
    }

    /**
     *
     *    Displays the values of the corresponding properties.
    **/
    private void processDCommand()
    {
        if(paramNum_>1)
        {
            appendOutput(ResourceLoader.getText("REMOTE_COMMAND_ERROR")+"\n");
            appendOutput("\n"+ResourceLoader.getText("REMOTE_PROMPT"));
        }
        else
        {
            appendOutput(ResourceLoader.getText("REMOTE_D_LINE1")+"\n");
            appendOutput(ResourceLoader.getText("REMOTE_D_LINE2"));
            appendOutput(javaAppCall_.getSecurityCheckLevel()+"\n");
            appendOutput(ResourceLoader.getText("REMOTE_D_LINE3"));
            appendOutput(classPath_+"\n");
            appendOutput(ResourceLoader.getText("REMOTE_D_LINE4"));
            appendOutput(javaAppCall_.getGarbageCollectionFrequency()+"\n");
            appendOutput(ResourceLoader.getText("REMOTE_D_LINE5"));
            appendOutput(javaAppCall_.getGarbageCollectionInitialSize()+"\n");
            appendOutput(ResourceLoader.getText("REMOTE_D_LINE6"));
            appendOutput(javaAppCall_.getGarbageCollectionMaximumSize()+"\n");
            appendOutput(ResourceLoader.getText("REMOTE_D_LINE7"));
            appendOutput(javaAppCall_.getGarbageCollectionPriority()+"\n");
            appendOutput(ResourceLoader.getText("REMOTE_D_LINE8"));
            appendOutput(javaAppCall_.getInterpret()+"\n");
            appendOutput(ResourceLoader.getText("REMOTE_D_LINE9"));
            appendOutput(javaAppCall_.getOptimization()+"\n");
            appendOutput(ResourceLoader.getText("REMOTE_D_LINE10"));
            // get the value of Options
            StringBuffer optionStr = new StringBuffer();
            for(int i=0;i< optionArray_.length;i++)
            {
                optionStr.append(optionArray_[i]);
                optionStr.append(" ");
            }
            appendOutput(optionStr.toString() +"\n");

            appendOutput(ResourceLoader.getText("REMOTE_D_LINE11"));
            appendOutput(javaAppCall_.getDefaultPort()+"\n");
            appendOutput(ResourceLoader.getText("REMOTE_D_LINE12"));
            appendOutput(javaAppCall_.isFindPort()+"\n");

            appendOutput("\n"+ResourceLoader.getText("REMOTE_PROMPT"));
        }
    }


    /**
     * Invoked when the user presses the enter key.
     *
     * @param event The ActionEvent.
    **/
    void processEnter()
    {
        if (javaAppCall_ == null)
           throw new ExtendedIllegalStateException("JavaApplicationCall",
            ExtendedIllegalStateException.PROPERTY_NOT_SET);

        // check the application whether completed
        if(javaRunOver_)
        {
            if(inputText_.getText().trim().equals(""))// process Return key
            {
                appendOutput("\n"+ResourceLoader.getText("REMOTE_PROMPT"));
            }
            else
            {
                if(inputVector_.size() >0)
                {
                    int tempIndex = inputIndex_;
                    int vectorSize = inputVector_.size();

                    String lastInput    = (String)inputVector_.lastElement();
                    String currentInput = (String) inputVector_.elementAt(tempIndex);
                    // enter a command that is not the same as the last command
                    if(!lastInput.equalsIgnoreCase(inputText_.getText()))
                    {
                        inputVector_.addElement(inputText_.getText());
                    }
                    hasNext = false;             // Press Down, can not get next command.
                    hasPrevious = false;
                    vectorSize = inputVector_.size();
                    inputIndex_ = vectorSize -1 ;
                    // keep the original command and press enter
                    if((tempIndex < vectorSize -1)&&inputText_.getText().equals(currentInput))
                    {
                        inputIndex_ = tempIndex;
                        hasNext = true;         // Press Down , can get next command.
                    }
                }
                else
                {
                    inputVector_.addElement(inputText_.getText());
                    inputIndex_ = 0;
                    hasNext = false;
                    hasPrevious = false;
                }
                // Get the original height of the outputText_
                appendOutput(inputText_.getText()+"\n");
                inputStr_ = inputText_.getText().trim();
                inputText_.setText("");
                strToken_ = new StringTokenizer(inputStr_ ," ");
                paramNum_ = strToken_.countTokens();
                String commandStr = strToken_.nextToken().toUpperCase();
                if(commandStr.equals("JAVA"))
                {
                    processJavaCommand();
                }
                else if(commandStr.equals("D"))
                {
                     processDCommand();
                }
                else if(commandStr.equals("SET"))
                {
                    processSetCommand();
                }
                else if ((commandStr.equals("HELP")) ||
                         (commandStr.equals("?"))    ||
                         (commandStr.equals("H")))
                {
                    processHelpCommand();
                }
                else if ((commandStr.equals("QUIT")) || (commandStr.equals("Q")))
                {
                    if(paramNum_ > 1)
                    {
                        appendOutput(ResourceLoader.getText("REMOTE_COMMAND_ERROR")+"\n");
                        appendOutput("\n"+ResourceLoader.getText("REMOTE_PROMPT"));
                    }
                    else
                        System.exit(0);
                }
                else
                {
                    appendOutput(ResourceLoader.getText("REMOTE_COMMAND_ERROR")+"\n");
                    appendOutput("\n"+ResourceLoader.getText("REMOTE_PROMPT"));
                }
            }
        }
        else // This is data to be sent to the application.
        {
            appendOutput(inputText_.getText()+"\n");
            inputStr_ = inputText_.getText();
            inputText_.setText("");
            javaAppCall_.sendStandardInString(inputStr_);
        }
    }


    /**
     *
     *    Process the help command.
    **/
    private void processHelpCommand()
    {
        if(paramNum_>1)
        {
            appendOutput(ResourceLoader.getText("REMOTE_COMMAND_ERROR")+"\n");
            appendOutput("\n"+ResourceLoader.getText("REMOTE_PROMPT"));
        }
        else
        {
            appendOutput(ResourceLoader.getText("REMOTE_HELP"));
            appendOutput("\n"+ResourceLoader.getText("REMOTE_PROMPT"));
        }
    }

    /**
     *
     *    Process the java command.
    **/
    private void processJavaCommand()
    {
        Properties prop = new Properties(); ;
        String javaAppName = "" ;
        String classPath  = "";
        Vector param = new Vector();

        if(paramNum_ == 1)
        {
            appendOutput(ResourceLoader.getText("REMOTE_JAVA_ERROR")+"\n");
            appendOutput("\n"+ResourceLoader.getText("REMOTE_PROMPT"));
        }
        else
        {
            Vector cmdVector = new Vector();
            while(strToken_.hasMoreTokens())
            {
                cmdVector.addElement(strToken_.nextToken());
            }
            int vectorSize = cmdVector.size();

            // find the java application
            int javaIndex = 0;
            boolean findJavaApp = false;
            while(javaIndex < vectorSize)
            {
                if(!(( String) cmdVector.elementAt(javaIndex)).startsWith("-"))
                {
                    javaAppName = (String) cmdVector.elementAt(javaIndex);
                    findJavaApp = true;
                    break;
                }
                javaIndex += 1;
            }
            if(findJavaApp)   // java application is found
            {
                boolean findError     = false;
                boolean findClassPath = false;
                boolean findVerbose   = false;
                boolean findProperty  = false;
                int i = 0;
                while( i < javaIndex)
                {
                    if(( (String) cmdVector.elementAt(i)).toUpperCase().startsWith("-CLASSPATH"))
                    {
                        if (!findClassPath) // only allow setting classpath one time in one command string
                        {
                            findClassPath = true;
                            String propertyValue = (String) cmdVector.elementAt(i);
                            int index = propertyValue.indexOf("=");
                            // check the format of the classpath
                            if ((index >0)&& ( index +1 < propertyValue.length()))
                            {
                                classPath = propertyValue.substring(index+1);
                            }
                            else
                            {
                                findError = true;
                                break;
                            }
                        }
                        else
                        {
                            findError = true;
                            break;
                        }
                     }
                     else if(( (String) cmdVector.elementAt(i)).toUpperCase().startsWith("-D"))
                     {
                        String tempStr = (String) cmdVector.elementAt(i);
                        String propStr = tempStr.substring(2);
                        StringTokenizer propToken = new StringTokenizer(propStr,"=");

                        if ( propToken.countTokens() >= 2)
                        {
                            String propKey       = propToken.nextToken();
                            String propertyValue = (String) cmdVector.elementAt(i);
                            int index            = propertyValue.indexOf("=");
                            String propValue     = propertyValue.substring(index+1);
                            prop.put(propKey,propValue);
                        }
                        else
                        {
                            findError = true;
                            break;
                        }
                        findProperty = true;
                    }
                    else if(( (String) cmdVector.elementAt(i)).toUpperCase().startsWith("-VERBOSE"))
                    {
                        if (!findVerbose)
                        {
                            findVerbose = true;
                        }
                        else
                        {
                            findError = true;
                            break;
                        }
                     }
                     else
                     {
                         findError = true;
                         break;
                     }
                     i += 1;
                }
                // find the parameters of the java application
                i = javaIndex + 1;
                boolean findParam = false;
                while(( i < vectorSize) && !findError)
                {
                    findParam = true;
                    param.addElement(cmdVector.elementAt(i));
                    i += 1;
                }
                if(!findError) // No error in the command string.
                {
                    try
                    {
                        javaAppCall_.setJavaApplication(javaAppName);
                        if (findParam)
                        {
                            String [] paramArray = new String[param.size()];
                            for(int j=0;j<param.size();j++)
                            {
                                paramArray[j] = (String) param.elementAt(j);
                            }
                            javaAppCall_.setParameters(paramArray);
                        }
                        if (findProperty)
                            javaAppCall_.setProperties(prop);
                        if (findClassPath)
                            javaAppCall_.setClassPath(classPath);
                        else
                            javaAppCall_.setClassPath(classPath_);
                        if (findVerbose)
                        {
                            String[] tempArray = new String[1];
                            tempArray[0] = "*VERBOSE";
                            javaAppCall_.setOptions(tempArray);
                        }
                        else
                            javaAppCall_.setOptions(optionArray_);
                        load();
                    }
                    catch(Exception e)
                    {
                        appendOutput(e+"\n");
                        appendOutput("\n"+ResourceLoader.getText("REMOTE_PROMPT"));
                    }
                }
                else
                {
                    appendOutput(ResourceLoader.getText("REMOTE_JAVA_ERROR")+"\n");
                    appendOutput("\n"+ResourceLoader.getText("REMOTE_PROMPT"));
                }
            }
            else
            {
                appendOutput(ResourceLoader.getText("REMOTE_JAVA_ERROR")+"\n");
                appendOutput("\n"+ResourceLoader.getText("REMOTE_PROMPT"));
            }
        }
    }

    /**
     *    Process the set command.
     **/
    private void processSetCommand()
    {
        if(paramNum_ == 1)
        {
            //appendOutput(ResourceLoader.getText("REMOTE_SET_ERROR")+"\n");
            //appendOutput("\n"+ResourceLoader.getText("REMOTE_PROMPT"));
            processDCommand();
        }
        else
        {
            String tempStr = inputStr_;
            String propertyName = null;
            String propertyValue = null;
            int beginIndex = tempStr.toUpperCase().indexOf("SET");
            String setString = inputStr_.substring(beginIndex+4).trim();
            StringTokenizer setStringToken = new StringTokenizer(setString,"=");
            int setNum = setStringToken.countTokens();
            if( setNum ==1)
            {
                appendOutput(ResourceLoader.getText("REMOTE_SET_ERROR")+"\n");
                appendOutput("\n"+ResourceLoader.getText("REMOTE_PROMPT"));
            }
            else
            {
                   // Sets the values of the corresponding properties.
                propertyName  = setStringToken.nextToken().toUpperCase().trim();
                propertyValue = setStringToken.nextToken().trim();
                try
                {
                    if(propertyName.equals("SECURITYCHECKLEVEL"))
                        javaAppCall_.setSecurityCheckLevel(propertyValue);

                    else if(propertyName.equals("CLASSPATH"))
                    {
                        int index = setString.indexOf("=");
                        String classStr = setString.substring(index+1).trim();
                        javaAppCall_.setClassPath(classStr);
                        classPath_ = classStr;
                    }
                    else if(propertyName.equals("GARBAGECOLLECTIONFREQUENCY"))
                        javaAppCall_.setGarbageCollectionFrequency(Integer.valueOf(propertyValue).intValue());

                    else if(propertyName.equals("GARBAGECOLLECTIONINITIALSIZE"))
                        javaAppCall_.setGarbageCollectionInitialSize(Integer.valueOf(propertyValue).intValue());

                    else if(propertyName.equals("GARBAGECOLLECTIONMAXIMUMSIZE"))
                        javaAppCall_.setGarbageCollectionMaximumSize(propertyValue);

                    else if(propertyName.equals("GARBAGECOLLECTIONPRIORITY"))
                        javaAppCall_.setGarbageCollectionPriority(Integer.valueOf(propertyValue).intValue());

                    else if(propertyName.equals("INTERPRET"))
                        javaAppCall_.setInterpret(propertyValue);

                    else if(propertyName.equals("OPTIMIZE"))
                        javaAppCall_.setOptimization(propertyValue);

                    else if(propertyName.equals("OPTION"))
                    {
                        StringTokenizer optionToken = new StringTokenizer(propertyValue," ");
                        String [] tempArray = new String[optionToken.countTokens()];
                        int j=0;
                        while(optionToken.hasMoreTokens())
                        {
                            tempArray[j] = optionToken.nextToken().toUpperCase();
                            j +=1;
                        }
                        javaAppCall_.setOptions(tempArray);
                        optionArray_ = new String[tempArray.length];
                        System.arraycopy(tempArray,0,optionArray_,0,tempArray.length);
                    }

                    else if(propertyName.equals("DEFAULTPORT"))
                        javaAppCall_.setDefaultPort(Integer.valueOf(propertyValue).intValue());

                    else if(propertyName.equals("FINDPORT"))
                    {
                        if((propertyValue.toUpperCase().equals("TRUE"))|| (propertyValue.toUpperCase().equals("FALSE")))
                            javaAppCall_.setFindPort(Boolean.valueOf(propertyValue).booleanValue());
                        else
                            appendOutput(ResourceLoader.getText("REMOTE_PORT_VALUE_ERROR")+"\n");
                    }
                    else
                    {
                        appendOutput(ResourceLoader.getText("REMOTE_PROPERTY_ERROR_HEAD")+propertyName+
                        ResourceLoader.getText("REMOTE_PROPERTY_ERROR_END")+"\n");
                    }
                }
                catch(Exception e1)
                {
                    appendOutput(e1.toString()+"\n");
                    Trace.log(Trace.ERROR,e1.toString());
                }
                appendOutput("\n"+ResourceLoader.getText("REMOTE_PROMPT"));
            }
        }
    }

    /**
     * Removes an error listener.
     *
     * @see #addErrorListener
     * @param listener The listener.
    **/
    public void removeErrorListener( ErrorListener listener )
    {
        if(listener == null)
        {
            throw new NullPointerException("ErrorListener");
        }
        errorEventSupport_.removeErrorListener(listener);
    }

    /**
     * Removes a property change listener.
     *
     * @see #addPropertyChangeListener
     * @param listener The listener.
    **/
    public void removePropertyChangeListener( PropertyChangeListener listener )
    {
        if (listener == null)
        {
            throw new NullPointerException("listener");
        }
        propertyChangeSupport_.removePropertyChangeListener(listener);
    }

    /**
     * Removes a vetoable change listener.
     *
     * @see #addVetoableChangeListener
     * @param listener The listener.
    **/
    public void removeVetoableChangeListener( VetoableChangeListener listener )
    {
        if (listener == null)
        {
            throw new NullPointerException("listener");
        }
        vetoableChangeSupport_.removeVetoableChangeListener(listener );
    }

    /**
     * Removes a working listener.
     *
     * @param listener The listener.
    **/
    public void removeWorkingListener(WorkingListener listener)
    {
        if (listener == null)
        {
            throw new NullPointerException("listener");
        }
        workingEventSupport_.removeWorkingListener(listener);
    }

    /**
     *  Sets the JavaApplicationCall object.
     *
     *  @param javaApplicationCall A com.ibm.as400.access.JavaApplicationCall
     *                object which defines the Java application to call.
     *  @exception PropertyVetoException If the change is voted.
    **/
    public void setJavaApplicationCall(JavaApplicationCall javaApplicationCall)
                throws PropertyVetoException
    {
        if(javaApplicationCall == null)
            throw new NullPointerException ("JavaApplicationCall");
        javaAppCall_ = javaApplicationCall;
    }

     /**
      *  The Java application is complete.
      *
      *  @param value True if the Java application is complete.  False if
      *                       the remote java application is still running..
      **/
      void setJavaAppRunOver(boolean value)
      {
          javaRunOver_ = value;
      }
}


