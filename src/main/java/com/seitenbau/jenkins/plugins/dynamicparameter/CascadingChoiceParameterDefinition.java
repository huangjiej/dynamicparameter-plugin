/*
 * Copyright 2012 Seitenbau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.seitenbau.jenkins.plugins.dynamicparameter;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.StringParameterValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.jvnet.localizer.ResourceBundleHolder;
import org.kohsuke.stapler.DataBoundConstructor;

import com.seitenbau.jenkins.plugins.dynamicparameter.util.JenkinsUtils;

/** 
 * A Choice parameter whose values are dependent on the values of another property. 
 * This class handles the configuration of the child property. 
 * The CascadingChoiceParameterValues class handles the process of actually retrieving the values.
 * @author Madeline Goss - madeline.goss@gmail.com
 */
public class CascadingChoiceParameterDefinition extends ChoiceParameterDefinition
{

  private static final long serialVersionUID = 1L;

  /** The name of the parent property. */
  private final String _parentPropertyName;
  
  /**
   * Constructor.
   * @param name parameter name
   * @param parentPropertyName the name of the parent property
   * @param script script, which generates the parameter value
   * @param description parameter description
   * @param uuid identifier (optional)
   * @param remote execute the script on a remote node
   */
  @DataBoundConstructor
  public CascadingChoiceParameterDefinition(String name, String parentPropertyName, String script, String description, String uuid,
      Boolean remote, Boolean readonlyInputField, String classPath)
  {
    super(name, script, description, uuid, remote, readonlyInputField, classPath, null);
    _parentPropertyName = parentPropertyName;
  }

  /**
   * Get the name of the parent property.
   * @return the parent property name as string
   */
  public final String getParentPropertyName()
  {
    return _parentPropertyName;
  }

  /** Parameter descriptor. */
  @Extension
  public static final class DescriptorImpl extends ParameterDescriptor
  {
    private static final String DISPLAY_NAME = "DisplayName";

    @Override
    public final String getDisplayName()
    {
      return ResourceBundleHolder.get(CascadingChoiceParameterDefinition.class).format(DISPLAY_NAME);
    }
  }

  /**
   * Get the possible choices, generated by the script with the parent property value as arg[0].
   * @return list of values if the script returns a non-null list;
   *         {@link Collections#EMPTY_LIST}, otherwise
   */
  public final List<Object> getChoices(String parentPropertyValue)
  {
    HashMap<String, String> parameters = new HashMap<String, String>();
    parameters.put(getParentPropertyName(), parentPropertyValue);
    return getScriptResultAsList(parameters);
  }
  
  /**
   * Get the identifying name of the project being run/configured
   * 
   */
  public final String getProjectName()
  {
    AbstractProject<?,?> project = JenkinsUtils.findCurrentProject(this.getUUID());
    if(project != null)
    {
      return project.getName();
    }
    return null;
  }

}
