package com.bayride.presentation.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.bayride.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChildNavigationHostFragment : Fragment(R.layout.child_nav_host_fragment) {
    companion object {
        private const val GRAPH_ID_KEY = "GRAPH_ID_KEY"

        fun newInstance(graphId: Int): ChildNavigationHostFragment {
            return ChildNavigationHostFragment().apply {
                arguments = Bundle().apply { putInt(GRAPH_ID_KEY, graphId) }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController =
            NavHostFragment.findNavController(childFragmentManager.findFragmentById(R.id.childNavHostContainer) as NavHostFragment)

        val hasGraph = try {
            navController.graph
            true
        } catch (e: IllegalStateException) {
            false
        }

        if (!hasGraph) {
            navController.setGraph(requireArguments().getInt(GRAPH_ID_KEY))
        }
    }
}