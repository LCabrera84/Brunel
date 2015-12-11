/*
 * Copyright (c) 2015 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
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

package org.brunel.build.d3.diagrams;

import org.brunel.build.ElementDependency;
import org.brunel.build.d3.ElementDefinition;
import org.brunel.build.util.ElementDetails;
import org.brunel.build.util.ScriptWriter;
import org.brunel.data.Dataset;
import org.brunel.model.VisSingle;

class Network extends D3Diagram {

    private final ElementDependency dependency;

    public Network(VisSingle vis, Dataset data, ElementDependency dependency, ScriptWriter out) {
        super(vis, data, out);
        this.dependency = dependency;
    }

    public ElementDetails writeDataConstruction() {

        String nodeField = quoted(vis.fKeys.get(0).asField());

        VisSingle links = dependency.getEdgeElement();
        String edgeDataset = dependency.linkedDataReference(links);
        String a = quoted(links.fKeys.get(0).asField());
        String b = quoted(links.fKeys.get(1).asField());

        out.add("var graph = BrunelData.diagram_Graph.make(processed,", nodeField, ",",
                edgeDataset, ",", a, ",", b, ")").endStatement();

        makeLayout();

        out.add("data._graph = graph").endStatement();

        return ElementDetails.makeForDiagram("graph.nodes", "circle", "point", "box", false);
    }

    private void makeLayout() {
        out.comment("Circle Layout")
                .add("var r = 0.5 - 5 / geom.inner_radius;")
                .add("var a, i, N = graph.nodes.length").endStatement()
                .add("for(i=0; i<N; i++) { a = Math.PI*2*i/N; graph.nodes[i].x = 0.5 + r*Math.cos(a); graph.nodes[i].y = 0.5 + r*Math.sin(a) }").endStatement();
    }

    public void writeDefinition(ElementDetails details, ElementDefinition elementDef) {
        out.addChained("attr('cx', function(d) { return scale_x(d.x) })")
                .addChained("attr('cy', function(d) { return scale_y(d.y) })")
                .addChained("attr('r',", elementDef.overallSize, ")");

        out.endStatement();
        addAestheticsAndTooltips(details, true);
    }

}
